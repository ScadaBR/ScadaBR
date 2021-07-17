# ScadaBR

### Você pode baixar a versão mais atual do ScadaBR [aqui](https://github.com/ScadaBR/ScadaBR/releases/latest). Dificilmente eu lançarei novas versões em um futuro próximo (motivo: fui pra faculdade).

## Sobre
O ScadaBR A principal intenção desse projeto é gerar uma versão do ScadaBR estável e mais atualizada com as tecnologias disponíveis em 2021, através de um front-end com várias melhorias de visual e usabilidade. Além disso, o ScadaBR-EF traz diversas correções de pequenos bugs que melhoram a experiência do usuário no dia a dia.

## Instalação
O ScadaBR tem instaladores para Windows e Linux, obtenha-os na [página dos releases](https://github.com/ScadaBR/ScadaBR/releases/latest/).

Se você quiser ou precisar realizar uma instalação manual, siga estes passos:
- Instale o Java (ou [OpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk8&jvmVariant=hotspot)) 8
- Instale o [Tomcat 9.0][Tomcat 9](https://tomcat.apache.org/download-90.cgi)
- Faça o download do [último release](https://github.com/ScadaBR/ScadaBR/releases/latest/)
- Extraia o arquivo `.war` e copie a pasta extraída para dentro da pasta `webapps/`, no Tomcat
- Reinicie o Tomcat

Obs.: o banco de dados usado por padrão é o Derby. Caso você queira utilizar outro banco de dados (como o MySql) a configuração a ser realizada é a mesma que seria feita para outras versões do ScadaBR (isto é, editar o arquivo `/WEB-INF/classes/env.properties`).

## O ScadaBR é estável?
Em teoria, sim. Uma vez que o foco desse projeto é melhorar o front-end do ScadaBR, poucas foram as alterações no código Java em si. Portanto, o ScadaBR-EF deveria ser tão estável quanto o ScadaBR 1.1 CE, no qual foi baseado. Entretanto, como eu não pude realizar muitos testes em cenários reais (dos protocolos de comunicação), não é possível garantir que o ScadaBR-EF realmente funcione de forma satisfatória para todos os casos.

## ScadaBR 1.2 vs. ScadaBR 1.1CE vs. Scada-LTS
O ScadaBR 1.2 não foi criado com qualquer intenção de ser um concorrente do Scada-LTS ou uma divisão no projeto ScadaBR original. O Scada-LTS é muito superior ao ScadaBR-EF, entretanto, como seu desenvolvimento ainda não chegou a uma versão "final" estável, e como o ScadaBR atualmente está preso ao Java 6, o ScadaBR foi pensado para possivelmente poder ser adotado como um "intermediário" para preencher a lacuna entre o ScadaBR atual (1.0/1.1) e o futuro Scada-LTS.


Recursos   | ScadaBR 1.0 | ScadaBR 1.1 | ScadaBR 1.2 | Scada-LTS
---------- | ----------- | ----------- | ---------- | ---------
Versão do Java | 6 | 7 ou 8 (depende da compilação) | 8 | 11
Página "perfil de usuário" | Não possui | Possui | Possui | Possui
API REST | Não possui | Não possui | Não possui | Possui
Desenvolvimento ativo | Não | Não | Não (apenas lançamentos eventuais) | Sim
Suporte a Modbus Serial | Sim | Não | Sim (Release 3+) | Não (mas será implementado no futuro)
Instaladores | Windows, Linux (não oficial), instalação manual | Sem instaladores oficiais, apenas instalação manual ou instaladores não oficiais | Windows, Linux (inclusive Raspberry), instalação manual | Instalação via Docker ou instalação manual


## Em que o ScadaBR roda?
Eu estou executando com sucesso o ScadaBR-EF no Linux Mint 19.1 (baseado no Ubuntu 18.04) com OpenJDK 8 (equivale ao Java 8) e Tomcat 8/9.
Meus arquivos **.war** foram compilados no Eclipse, versão 2020-12 (4.18.0).
O código-fonte eu puxei do SourceForge e trouxe para o GitHub. Como eu não sei muito sobre Java e Eclipse, não sei dizer esse repositório pode ser clonado e compilar em outro computador sem precisar alterar alguma configuração.

## Bugs conhecidos
- Eu experimentei problemas com o OpenJDK 8 na hora de enviar e-mails. Caso você receba um alarme de erro contendo a mensagem `javax.net.ssl.SSLHandshakeException: No appropriate protocol (protocol is disabled or cipher suites are inappropriate)` edite o arquivo `java.security` (que deve estar em `$JRE/lib/security/java.security`, no meu caso estava em `/etc/java-8-openjdk/security/java.security`) e, na opção `jdk.tls.disabledAlgorithms` remova `TLSv1` e `TLSv1.1` da lista.
- Você só pode abrir uma Representação Gráfica por vez no mesmo navegador. Essa limitação, herdada do ScadaBR, é bastante complexa e eu não consegui resolvê-la no ScadaBR-EF, infelizmente.
