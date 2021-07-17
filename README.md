# ScadaBR

### Você pode baixar a versão mais atual do ScadaBR [aqui](https://github.com/ScadaBR/ScadaBR/releases/latest). .

## Sobre
O ScadaBR pretende oferecer todas as funcionalidades de um sistema SCADA tradicional. Este tipo de software (Supervisory Control and Data Acquisition, na sigla em inglês) existe desde o final dos anos 60, e é a peça fundamental em qualquer tipo de aplicação computadorizada que envolva máquinas, controladores programáveis (CLP´s), acionamentos eletrônicos e sensores.

## Instalação
O ScadaBR tem instaladores para Windows e Linux, obtenha-os na [página dos releases](https://github.com/ScadaBR/ScadaBR/releases/latest/).

Se você quiser ou precisar realizar uma instalação manual, siga estes passos:
- Instale o Java (ou [OpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk8&jvmVariant=hotspot)) 8
- Instale o [Tomcat 9.0][Tomcat 9](https://tomcat.apache.org/download-90.cgi)
- Faça o download do [último release](https://github.com/ScadaBR/ScadaBR/releases/latest/)
- Extraia o arquivo `.war` e copie a pasta extraída para dentro da pasta `webapps/`, no Tomcat
- Reinicie o Tomcat

Obs.: O banco de dados usado por padrão é o Derby. Caso você queira utilizar outro banco de dados (como o MySql) a configuração a ser realizada é a mesma que seria feita para outras versões do ScadaBR (isto é, editar o arquivo `/WEB-INF/classes/env.properties`) e instale o ConnectorJ referente a seu gerenciador de Banco de dados.

## O ScadaBR é estável?
Em teoria, sim. Uma vez que o foco desse projeto é melhorar o front-end do ScadaBR, poucas foram as alterações no código Java em si. Portanto, o ScadaBR deveria ser tão estável quanto o ScadaBR 1.1 CE, no qual foi baseado. Entretanto, contudo, Não foi testado todas as funcionalidades desta versão. 

## Situação atual do ScadaBR 

Recursos   | ScadaBR 1.0 | ScadaBR 1.1 | ScadaBR 1.2 | Scada-LTS
---------- | ----------- | ----------- | ---------- | ---------
Versão do Java | 6 | 7 ou 8 (depende da compilação) | 8 | 11
Página "perfil de usuário" | Não possui | Possui | Possui | Possui
API REST | Não possui | Não possui | Não possui | Possui
Desenvolvimento ativo | Não | Não | Não (apenas lançamentos eventuais) | Sim
Suporte a Modbus Serial | Sim | Não | Sim | Não (mas será implementado no futuro)
Instaladores | Windows, Linux (não oficial), instalação manual | Sem instaladores oficiais, apenas instalação manual ou instaladores não oficiais | Windows, Linux (inclusive Raspberry), instalação manual | Instalação via Docker ou instalação manual


## Em que o ScadaBR roda?
Eu estou executando com sucesso o ScadaBR no Linux Mint 19.1 (baseado no Ubuntu 18.04), Ubuntu 20.04, Raspberry PI 3 usando raspbian com OpenJDK 8 (equivale ao Java 8) e Tomcat 8 e 9.
O arquivo **.war** foi compilado no Eclipse, versão 2020-12 (4.18.0).
Os códigos-fontes foram copiados do SourceForge e agora estão aqui no GitHub. 


## Bugs conhecidos
- Foram reportado problemas com o OpenJDK 8 na hora de enviar e-mails. Caso você receba um alarme de erro contendo a mensagem `javax.net.ssl.SSLHandshakeException: No appropriate protocol (protocol is disabled or cipher suites are inappropriate)` edite o arquivo `java.security` (que deve estar em `$JRE/lib/security/java.security`, no meu caso estava em `/etc/java-8-openjdk/security/java.security`) e, na opção `jdk.tls.disabledAlgorithms` remova `TLSv1` e `TLSv1.1` da lista.
- Você só pode abrir uma Representação Gráfica por vez no mesmo navegador. Essa limitação, herdada do ScadaBR, é bastante complexa, infelizmente, Caso precise de várias telas, utilize Navegadores diferentes.
