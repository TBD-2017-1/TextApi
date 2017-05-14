# TextApi for PoliTweetsCL

Text API, es la libreria encargada de controllar los incides invertidos en lucene, de modo que tiene la capacidad de indexar
y consultar los indices invertidos. El indice que se ocupa no es persistente y se crea cada vez que se vuelve a indexar.
Esto debido a que la logica de PoliTweets es generar metricas diariamente, no existe reutilizacion de indices.

## Instalación
Para compilar el protecto se requiere:
- Gradle 3.5
- Maven 3.5
- Libreria [Core v1.5](https://github.com/TBD-2017-1/Core.git) de este mismo proyecto 
- Clomar este repositorio con `git clone https://github.com/TBD-2017-1/TextApi.git`


## Compilación y uso
La version que el BackEnd de PoliTweets utiliza es la v1.2, y viene integrada por defecto en el repositorio de [BackEnd](https://github.com/TBD-2017-1/PoliTweets.git).

Si se quiere recompilar el proyecto, se debe escribir en la raiz de este repositorio y por terminal
```
gradle shadow
```

Para actualizar la libreria en BackEnd:
 - Copiar la libreria que generada en el proceso de compilacion, ubicada en la carpeta `lib/`
 - Pegar en la carpeta `lib/` del Proyecto de BackEnd