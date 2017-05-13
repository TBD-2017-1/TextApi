import PoliTweetsCL.Core.Model.Tweet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class LuceneImplementacion {

    public void CrearIndice(Tweet[] tweets){// metodo que crea el indice con todos los archivos dentro del path
        try {
            // Preparar un nuevo indice
            Directory dir = FSDirectory.open(Paths.get("indice/"));// directorio donde se guarda el indice
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter w = new IndexWriter(dir, config);

            // por cada tweet
            for (Tweet tweet : tweets) {
                Document doc = new Document();

                // obtener los datos necesarios
                String texto = tweet.getText();

                // agregarlos al documento
                doc.add(new TextField("texto", texto, Field.Store.YES));

                // Agregar documento al indice
                w.addDocument(doc);
            }

            //System.out.println("field: "+ w.getFieldNames());
            //System.out.println("numdoc: " + w.numDocs());

            // cerrar indice
            w.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
     
     
    public void BuscarIndice(String palabra) {// metodo para busqueda dada alguna palabra
        try {
            Directory dir = FSDirectory.open(Paths.get("indice/"));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("texto", analyzer);
            Query query = parser.parse(palabra);//la palabra que se quiere buscar
            //Term t = new Term("texto", "lucene");
            //Query query = new TermQuery(t);
            TopDocs results = searcher.search(query, 5);
            ScoreDoc[] hits = results.scoreDocs;

            System.out.println("hits: " + hits.length);
            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                //String path = doc.get("path");
                System.out.println((i + 1) + ".- score=" + hits[i].score);

            }
            reader.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        
    LuceneImplementacion lucene = new LuceneImplementacion();
    //lucene.parseJSONFile();
    //lucene.CrearIndice("resources/test2.json");
    lucene.BuscarIndice("lucene");
  
  }

}
    

