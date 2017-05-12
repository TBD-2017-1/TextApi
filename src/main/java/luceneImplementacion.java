import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class LuceneImplementacion {
    /*
    public void parseJSONFile() throws FileNotFoundException, IOException, org.json.simple.parser.ParseException, JSONException{// parsea el archivo json
        
         JSONParser parser = new JSONParser();
         Object object = parser.parse(new FileReader("/home/gabriel/NetBeansProjects/luceneImplementacion/resources/test2.json"));   
        JSONArray jsonArray = (JSONArray) object;
        System.out.println("largo: "+ jsonArray.size());
        for(int i = 0 ; jsonArray.size() > i; i++){
            JSONObject jsonObjectRow = (JSONObject) jsonArray.get(i);
            String name = (String) jsonObjectRow.get("name");

        //String name = (String) jsonArray.get(i).toString();
        System.out.println(name);
        }
        //JSONArray array = (JSONArray) parser.parse(new FileReader("resources/test.json"));
        //System.out.println("tamaño array:"+array.length());
        
        for (int i =0 ; array.length()< i; i++){
        JSONObject jsonObject =  (JSONObject) o;
        String name = (String) jsonObject.get("name");
            System.out.println(name);
         String stored = (String) jsonObject.get("stored");
            System.out.println(stored);
        }
        
        InputStream jsonFile =  getClass().getResourceAsStream("resources/test.json");
        Reader readerJson = new InputStreamReader(jsonFile);
        Object fileObjects= JSONValue.parse(readerJson);
        JSONArray arrayObjects=(JSONArray)fileObjects;
        return arrayObjects;
        
    }
    */
    public void CrearIndice( String path) throws IOException, JSONException, org.json.simple.parser.ParseException{// metodo que crea el indice con todos los archivos dentro del path
    
       JSONParser parser = new JSONParser();
         Object object = parser.parse(new FileReader(path));   //path del archivo json
        JSONArray jsonArray = (JSONArray) object;
        //System.out.println("largo: "+ jsonArray.size());
        


        
    Directory dir = FSDirectory.open(Paths.get("indice/"));// directorio donde se guarda el indice
    StandardAnalyzer analyzer = new StandardAnalyzer();


    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter w = new IndexWriter(dir, config);
    String ruta = "resources/"; //ruta de los archivos que quieren ser indexados
    
    for(int i = 0 ; jsonArray.size() > i; i++){
            Document doc = new Document();
            JSONObject jsonObjectRow = (JSONObject) jsonArray.get(i);
            String texto = (String) jsonObjectRow.get("texto");
            doc.add(new TextField("texto", texto, Field.Store.YES));
        //String name = (String) jsonArray.get(i).toString();
        System.out.println(texto);
         
         w.addDocument(doc);
                
              
         //System.out.println("tengo:" +doc.get("texto"));
        }
        //System.out.println("field: "+ w.getFieldNames());
        System.out.println("numdoc: "+ w.numDocs());
    /*
    for(JSONObject object : (List<JSONObject>) arrayObjects){
        Document doc = new Document();
        for(String field : (Set<String>) object.keys()){
            Class type = object.get(field).getClass();
            if(type.equals(String.class)){
                doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
            }
        }
    }
   
    */
    /*if(Files.isDirectory(Paths.get(ruta))){
        File directorioTweets = new File(ruta);
        File[] Tweets = directorioTweets.listFiles();
        Document doc = null;
        for( File f : Tweets){
            if(f.isFile() && f.canRead() && f.getName().endsWith(".txt")){
                doc = new Document();
                doc.add(new StringField("path", f.toString(), Field.Store.YES));// añade el path del archivo que se esta indexando
                doc.add(new TextField("contenido", new FileReader(f))); // añade el contenido del arhcivo
                if(w.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE){
                    System.out.println("Indexando el archivo:" +f.getName());
                    w.addDocument(doc);
                }
                else{
                    System.out.println("Actualizando el archivo:" + f.getName());
                    w.updateDocument(new Term("path", f.toString()), doc);
                }
            }
        }
    }
        System.out.println("numero documentos indexados:" + w.numDocs());*/
    w.close();
    }
    
     
     
    public void BuscarIndice(String palabra) throws IOException, ParseException{// metodo para busqueda dada alguna palabra
        Directory dir = FSDirectory.open(Paths.get("indice/"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("texto", analyzer);
        Query query = parser.parse(palabra);//la palabra que se quiere buscar
        //Term t = new Term("texto", "lucene");
        //Query query = new TermQuery(t);
        TopDocs results = searcher.search(query,5);
        ScoreDoc[] hits = results.scoreDocs;
        
        System.out.println("hits: "+hits.length);
        for(int i= 0; i < hits.length; i++){
            Document doc = searcher.doc(hits[i].doc);
            //String path = doc.get("path");
            System.out.println((i+1)+".- score="+ hits[i].score);
            
        }
        reader.close();
    }
    public static void main(String[] args) throws IOException, ParseException, JSONException, FileNotFoundException, org.json.simple.parser.ParseException {
        
    LuceneImplementacion lucene = new LuceneImplementacion();
    //lucene.parseJSONFile();
    lucene.CrearIndice("resources/test2.json");
    lucene.BuscarIndice("lucene");
   
  
  }

    }
    

