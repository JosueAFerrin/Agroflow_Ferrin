@echo off
echo DEPLOY COMPLETO DE AGROFLOW EN KUBERNETES
echo ==========================================

:: Cambiar al directorio del script y luego subir un nivel
cd /d "%~dp0"
cd ..

echo.
echo PASO 1: Verificando herramientas...
echo ===================================
docker version
if %errorlevel% neq 0 (
    echo ERROR: Docker no funciona
    goto :error
)

kubectl version --client
if %errorlevel% neq 0 (
    echo ERROR: kubectl no funciona
    goto :error
)

echo EXITO: Todas las herramientas disponibles

echo.
echo PASO 2: Compilando microservicios...
echo ===================================

:: ms-central
echo [1/3] Compilando ms-central...
if exist "ms-central" (
    cd ms-central
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms-central
        goto :error
    )
    cd ..
    echo EXITO: ms-central compilado
) else (
    echo ERROR: Directorio ms-central no encontrado
    goto :error
)

:: ms-inventario
echo [2/3] Compilando ms-inventario...
if exist "ms-inventario" (
    cd ms-inventario
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms-inventario
        goto :error
    )
    cd ..
    echo EXITO: ms-inventario compilado
) else (
    echo ERROR: Directorio ms-inventario no encontrado
    goto :error
)

:: ms-facturacion
echo [3/3] Compilando ms-facturacion...
if exist "ms-facturacion" (
    cd ms-facturacion
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms-facturacion
        goto :error
    )
    cd ..
    echo EXITO: ms-facturacion compilado
) else (
    echo ERROR: Directorio ms-facturacion no encontrado
    goto :error
)

echo EXITO: Compilacion de microservicios completada

echo.
echo PASO 3: Construyendo imagenes Docker...
echo =======================================

echo [1/3] Construyendo ms-central...
docker build -t agroflow-ms-central:latest ./ms-central
if %errorlevel% neq 0 (
    echo ERROR: Fallo construccion de ms-central
    goto :error
)
echo EXITO: Imagen ms-central creada

echo [2/3] Construyendo ms-inventario...
docker build -t agroflow-ms-inventario:latest ./ms-inventario
if %errorlevel% neq 0 (
    echo ERROR: Fallo construccion de ms-inventario
    goto :error
)
echo EXITO: Imagen ms-inventario creada

echo [3/3] Construyendo ms-facturacion...
docker build -t agroflow-ms-facturacion:latest ./ms-facturacion
if %errorlevel% neq 0 (
    echo ERROR: Fallo construccion de ms-facturacion
    goto :error
)
echo EXITO: Imagen ms-facturacion creada

echo EXITO: Imagenes Docker completadas

echo.
echo PASO 4: Desplegando en Kubernetes...
echo ===================================

echo [1/6] Creando namespace agroflow...
kubectl apply -f k8s\00-namespace.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al crear namespace
    goto :error
)
echo EXITO: Namespace creado
timeout /t 5 /nobreak > nul

echo [2/6] Desplegando PostgreSQL...
kubectl apply -f k8s\01-postgresql.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar PostgreSQL
    goto :error
)
echo Esperando que PostgreSQL este listo (hasta 5 minutos)...
kubectl wait --for=condition=ready pod -l app=postgresql -n agroflow --timeout=300s
if %errorlevel% neq 0 (
    echo ADVERTENCIA: PostgreSQL tomo mas tiempo del esperado, verificando estado...
    kubectl get pods -l app=postgresql -n agroflow
) else (
    echo EXITO: PostgreSQL esta listo
)

echo [3/6] Desplegando RabbitMQ...
kubectl apply -f k8s\02-rabbitmq.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar RabbitMQ
    goto :error
)
echo Esperando que RabbitMQ este listo (hasta 5 minutos)...
kubectl wait --for=condition=ready pod -l app=rabbitmq -n agroflow --timeout=300s
if %errorlevel% neq 0 (
    echo ADVERTENCIA: RabbitMQ tomo mas tiempo del esperado, verificando estado...
    kubectl get pods -l app=rabbitmq -n agroflow
) else (
    echo EXITO: RabbitMQ esta listo
)

echo [4/6] Desplegando ms-central...
kubectl apply -f k8s\03-ms-central.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar ms-central
    goto :error
)
echo EXITO: ms-central desplegado
timeout /t 60 /nobreak > nul

echo [5/6] Desplegando ms-inventario...
kubectl apply -f k8s\04-ms-inventario.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar ms-inventario
    goto :error
)
echo EXITO: ms-inventario desplegado
timeout /t 60 /nobreak > nul

echo [6/6] Desplegando ms-facturacion...
kubectl apply -f k8s\05-ms-facturacion.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar ms-facturacion
    goto :error
)
echo EXITO: ms-facturacion desplegado
timeout /t 60 /nobreak > nul

echo [EXTRA] Desplegando Ingress...
kubectl apply -f k8s\06-ingress.yaml
if %errorlevel% neq 0 (
    echo ADVERTENCIA: Fallo al desplegar Ingress (verifica nginx-ingress)
) else (
    echo EXITO: Ingress desplegado
)

echo.
echo =========================================
echo DEPLOY COMPLETADO EXITOSAMENTE!
echo =========================================
echo.
echo Esperando 60 segundos para que todos los servicios terminen de arrancar...
timeout /t 60 /nobreak > nul

echo Estado final de los pods:
kubectl get pods -n agroflow
echo.
echo Servicios disponibles:
kubectl get services -n agroflow
echo.
echo =========================================
echo COMANDOS PARA ACCEDER A LOS SERVICIOS:
echo =========================================
echo.
echo Para ms-central:
echo kubectl port-forward service/ms-central-svc 8081:8081 -n agroflow
echo.
echo Para ms-inventario:
echo kubectl port-forward service/ms-inventario-svc 8082:8082 -n agroflow
echo.
echo Para ms-facturacion:
echo kubectl port-forward service/ms-facturacion-svc 8083:8083 -n agroflow
echo.
echo Para RabbitMQ Management:
echo kubectl port-forward service/rabbitmq-svc 15672:15672 -n agroflow
echo.
echo =========================================
echo URLS DE ACCESO:
echo =========================================
echo ms-central: http://localhost:8081
echo ms-inventario: http://localhost:8082
echo ms-facturacion: http://localhost:8083
echo RabbitMQ Management: http://localhost:15672 (usuario: admin, clave: admin123)
echo.
echo Si configuraste nginx-ingress:
echo AgroFlow: http://agroflow.local
echo.
echo =========================================
echo DEPLOY COMPLETADO - El script terminara en 10 segundos
echo =========================================
timeout /t 10 /nobreak > nul
goto :end

:error
echo.
echo =========================================
echo ERROR EN EL DEPLOY
echo =========================================
echo El script se detendra en 30 segundos para revisar el error
timeout /t 30 /nobreak > nul
exit /b 1

:end
echo Script finalizado exitosamente
exit /b 0