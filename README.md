# inventory-app
# Aplicacion CRUD para la administracion de productos y su inventario, construida con java17, springboot, H2, AWS SQS, Docker y AWS ECS.


## Caracteristicas Microservicios Product
- Agregar, editar, y borrar productos
- Consulta detalle de un producto por Id
- Comunicacion asincrona con Inventory para enviar stock de un producto


## Caracteristicas Microservicios Inventory
- Microservicio Inventory
- Agregar, editar, y borrar stock
- Consultar detalle de stock de un  producto
- Comunicacion asincrona medinate mensajeria para actualizacion o alta de stock de un producto

## Requirements
- Java 17
- Spring boot 3
- Amazon SQS
- Amazon ECS
- Docker


## Installation de microservicio Inventory (repetir pasos para microservico Product)
1. Crear un repositorio ECR (si no existe)
aws ecr create-repository --repository-name inventory-app

2. Iniciar sesión en ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

3. Construir la imagen Docker
docker build -t inventory-app:v1 .

4. Etiquetar la imagen
docker tag mi-app:latest ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/mi-app-repo:latest

5. Subir la imagen a ECR
docker push ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/mi-app-repo:latest

6. Crear un cluster de Fargate (si no existe)
aws ecs create-cluster --cluster-name mi-cluster

7. Registrar una definición de tarea
aws ecs register-task-definition --cli-input-json file://task-definition.json

8. Crear un servicio de ECS con Fargate
aws ecs create-service \
  --cluster mi-cluster \
  --service-name mi-servicio \
  --task-definition mi-tarea:1 \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-12345678],securityGroups=[sg-12345678],assignPublicIp=ENABLED}" \
  --platform-version LATEST

9. Verificar si tu servicio tiene un balanceador de carga (Load Balancer) asociado:
    ecs describe-services --cluster mi-cluster --services mi-servicio
    - 9.1 Busca en la salida la sección loadBalancers.
    - 9.1.1  Si tiene un balanceador de carga, obtén su DNS:
    - Primero identifica el ARN del balanceador de carga
    - aws elbv2 describe-load-balancers --query "LoadBalancers[].DNSName"
    - Esta será la URL que puedes usar para acceder a tu servicio.
    - 9.1.2  Si no tiene un balanceador de carga pero has configurado un grupo de tareas con IP pública:
    - Obtén la tarea en ejecución
    - aws ecs list-tasks --cluster mi-cluster --service-name mi-servicio
    
    - Con el ARN de la tarea, describe sus detalles
    aws ecs describe-tasks --cluster mi-cluster --tasks [task-arn]


10. Crear un rol de ejecución de ECS (si no existe)
aws iam create-role --role-name ecsTaskExecutionRole --assume-role-policy-document file://trust-policy.json


11. Actualizar un servicio existente (para nuevos despliegues)
aws ecs update-service \
  --cluster mi-cluster \
  --service mi-servicio \
  --task-definition mi-tarea:2 \
  --force-new-deployment


12. Verificar el estado de la tarea en ejecución
aws ecs list-tasks --cluster mi-cluster --service-name mi-servicio


13. Se actualiza el rol de ejecución de ECS para permitir el acceso a ECR y CloudWatch Logs. Asegúrate d tener los permisos necesarios para realizar estas acciones.

- aws iam update-assume-role-policy --role-name ecsTaskExecutionRole --policy-document file://trust-policy.json
- aws iam attach-role-policy --role-name ecsTaskExecutionRole --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
- aws iam attach-role-policy --role-name ecsTaskExecutionRole --policy-arn arn:aws:iam::aws:policy/AmazonECR-FullAccess
- aws iam put-role-policy --role-name ecsTaskExecutionRole --policy-name ECRAccess --policy-document file://ecr-policy.json
  

14. Verificar los permisos del rol de ejecución de ECS
aws iam list-attached-role-policies --role-name ecsTaskExecutionRole
aws iam list-role-policies --role-name ecsTaskExecutionRole


15. Crear un grupo de logs en CloudWatch para almacenar los logs de la tarea de ECS. Asegúrate de tener los permisos necesarios para crear grupos de logs.
- aws logs create-log-group --log-group-name /ecs/inventory-task
- aws ecs describe-task-definition --task-definition  inventory-task:1
- aws iam attach-role-policy --role-name ecsTaskExecutionRole --policy-arn arn:aws:iam::aws:policy/- CloudWatchLogsFullAccess
- aws ecs update-service --cluster dev-cluster-inventory  --service inventory-service --task-definition inventory-task:1 --force-new-deployment

16. Obtén la tarea en ejecución
- aws ecs list-tasks --cluster dev-cluster-inventory --service-name  inventory-service

17. Con el ARN de la tarea, describe sus detalles
- aws ecs describe-tasks --cluster dev-cluster-inventory --tasks arn:aws:ecs:us-east-1::task/dev-cluster-inventory/b85d390392954e2ca6ad844039a02837


18. Actualiza el servicio para asignar IPs públicas si no está habilitado:
    bashaws ecs update-service \
      --cluster tu-cluster \
      --service tu-servicio \
      --network-configuration "awsvpcConfiguration={subnets=[subnet-id1,subnet-id2],securityGroups=[sg-id],assignPublicIp=ENABLED}" \
      --force-new-deployment

      
19. Verificar los logs de la tarea en CloudWatch
aws logs get-log-events --log-group-name /ecs/mi-servicio --log-stream-name [log-stream-name] --limit 10

20. Para verificar si la tarea tiene una IP pública asignada, puedes usar el siguiente comando:
aws ecs describe-tasks --cluster tu-cluster --tasks [task-arn] --query "tasks[0].attachments[0].details[?name=='privateIPv4Address'].[value]" --output text

- Asegúrate de que las subredes sean públicas:
    
  - Las subredes deben tener una ruta a un Internet Gateway en su tabla de rutas
  - Puedes verificar si una subred es pública con:
    ec2 describe-route-tables --filters "Name=association.subnet-id,Values=subnet-id"
    
  - Busca una ruta con destino 0.0.0.0/0 que apunte a un igw- (Internet Gateway)        
  - Si necesitas cambiar a subredes públicas:
    ecs update-service \
      --cluster tu-cluster \
      --service tu-servicio \
      --network-configuration "awsvpcConfiguration={subnets=[subnet-publica1,subnet-publica2],securityGroups=[sg-id],assignPublicIp=ENABLED}" \
      --force-new-deployment
    
23. Verifica el grupo de seguridad para asegurarte de que permite el tráfico necesario.


## Installation de cola mensajeria en SQS 

aws iam create-policy --policy-name ECSTaskSQSPolicy --policy-document file://sqs-policy.json

# Guarda el ARN de la política que se muestra en la salida
POLICY_ARN=$(aws iam create-policy --policy-name ECSTaskSQSPolicy --policy-document file://sqs-policy.json --query 'Policy.Arn' --output text)

# Adjunta la política al rol
aws iam attach-role-policy --role-name ECSTaskSQSRole --policy-arn $POLICY_ARN


