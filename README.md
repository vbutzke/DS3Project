ObseCão

Projeto de aplicativo para adoção de cães.
Este projeto contém o backend do aplicativo.

Plataforma: 
 - Android (Nativo)
 
Tecnologias: 
 - Java
 - Gradle
 - JUnit
 - MongoDB
 - Jackson
 - SpringBoot
 - Spring Security

Para executar o exemplo:

1.Abrir um terminal

2.Executar os comandos:
    
    $mkdir algumNomeDePasta
    
    $cd algumNomeDePasta
    
    $git clone https://github.com/vbutzke/DS3Project.git
    
    $./gradlew build && java -jar build/libs/gs-spring-boot-0.1.0.jar
  
  Em outra aba do terminal, executar:
    
    $curl localhost:8080
  
  Ou abrir manualmente um navegador no endereço:
    
    localhost:8080

3.Abrir o Postman e esecutar a seguinte requisição:
 
    PUT localhost:8080/register?email="emaildummytestersender@gmail.com"&firstName="Adopter"&lastName="A123"&password="adopter123"&passwordConf="adopter123"&code=""&response=

4.Verificar que o JSON foi retornado com sucesso. Para verificar o usuário no banco, abrir outra aba no terminal e executar:

    $mongo
    
    $use Obsecao
    
    $db.user.find({})
    
5.Na mesma aba, deletar o registro inserido:

    $db.user.delete({})

6.Executar no Postman a seguinte requisição para encerrar a conexão com o banco:

    GET localhost:8080/logout

7.CNTRL+C nos terminais

*Para que o projeto funcione, é necessário instalar o MongoDB (instruções abaixo).
Futuramente haverá uma imagem docker com todas as configurações de ambiente.

Para baixar o MongoDB, acesse o link:
  
    https://www.mongodb.com/download-center?initial=true#community
  
  Siga os passos de instalação, deixando todas as opções default. Dê preferência pela versão completa.
  
  Para um tutorial de instalação:
    
    https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/
  
  MongoDB Docs:
    
    https://docs.mongodb.com/manual/introduction/
   
Nome do banco: Obsecao

Coleção criada: user
