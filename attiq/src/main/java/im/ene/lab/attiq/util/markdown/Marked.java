package im.ene.lab.attiq.util.markdown;

import io.github.gitbucket.markedj.Lexer;
import io.github.gitbucket.markedj.Options;
import io.github.gitbucket.markedj.Parser;

/**
 * Created by eneim on 1/18/16.
 *
 * An extension of {@link io.github.gitbucket.markedj.Marked}, with better support for list items
 */
public class Marked {

  public static String marked(String src) {
    Options options = new Options();
    return marked(src, options, new Renderer(options));
  }

  public static String marked(String src, Options options) {
    return marked(src, options, new Renderer(options));
  }

  public static String marked(String src, Options options, Renderer
      renderer) {
    Lexer lexer = new Lexer(options);
    Lexer.LexerResult result = lexer.lex(src);
    Parser parser = new Parser(options, renderer);
    return parser.parse(result.getTokens(), result.getLinks());
  }
}
