
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
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.apache.lucene.document.IntField;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class LuceneImplementacion {
    
    
    
    public void CrearIndice(String path) throws IOException, JSONException, org.json.simple.parser.ParseException{// metodo que crea el indice con todos los archivos dentro del path
    
       JSONParser parser = new JSONParser();
         Object object = parser.parse(new FileReader(path));   //path del archivo json
        JSONArray jsonArray = (JSONArray) object;    
    Directory dir = FSDirectory.open(new File("indice/"));// directorio donde se guarda el indice
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);


    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48,analyzer);
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
   
    w.close();
    }
    
     public void CrearIndice(Tweet tweet[]) throws IOException, JSONException, org.json.simple.parser.ParseException{// metodo que crea el indice con todos los archivos dentro del path
     
    Directory dir = FSDirectory.open(new File("indice/"));// directorio donde se guarda el indice
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);


    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48,analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter w = new IndexWriter(dir, config);
    String ruta = "resources/"; //ruta de los archivos que quieren ser indexados
    
    for(int i = 0 ; tweet.length > i; i++){
            Document doc = new Document();
            
            String texto = tweet[i].text;
            
            doc.add(new TextField("texto", texto, Field.Store.YES));
            
        //String name = (String) jsonArray.get(i).toString();
        System.out.println(texto);
         
         w.addDocument(doc);
                
              
         //System.out.println("tengo:" +doc.get("texto"));
        }
        //System.out.println("field: "+ w.getFieldNames());
        System.out.println("numdoc: "+ w.numDocs());
   
    w.close();
    }
     
    public void BuscarIndice(String palabra) throws IOException, ParseException{// metodo para busqueda dada alguna palabra
        Directory dir = FSDirectory.open(new File("indice/"));
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_48,"texto", analyzer);
        Query query = parser.parse(palabra);//la palabra que se quiere buscar
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
    
    public void BuscarIndice(String keywords[]) throws IOException, ParseException{// metodo para busqueda dada alguna palabra
        Directory dir = FSDirectory.open(new File("indice/"));
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_48,"texto", analyzer);
        BooleanQuery bq =  new BooleanQuery();
        for(int i =0 ; keywords.length > i; i++){
           Query query = parser.parse(keywords[i]);//la palabra que se quiere buscar
           bq.add(query, BooleanClause.Occur.SHOULD);
        }
        TopDocs results = searcher.search(bq,5);
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
    /*
    Tweet t1 = new Tweet("probando lucene");
    Tweet t2 = new Tweet("lucene hola mani");
    Tweet t3 = new Tweet("probando que tal tu  lucene");
    Tweet t4 = new Tweet("probando jota erre lucene");
    Tweet t5 = new Tweet("probando kappa lul lucene");
    Tweet t6 = new Tweet("probando no mas esto");
    Tweet[] tweets = new Tweet[6];
    tweets[0] = t1;
    tweets[1] = t2;
    tweets[2] = t3;
    tweets[3] = t4;
    tweets[4] = t5;
    tweets[5] = t6;
    String[] keywords = {"hola","mani","volar"};
     */
    //lucene.parseJSONFile();
    //lucene.CrearIndice("resources/test2.json");
    //lucene.CrearIndice(tweets);
    //lucene.BuscarIndice(keywords);
   
  
  }

    }
    
