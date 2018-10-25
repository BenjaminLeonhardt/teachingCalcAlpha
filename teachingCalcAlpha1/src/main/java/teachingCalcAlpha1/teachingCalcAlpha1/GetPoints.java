package teachingCalcAlpha1.teachingCalcAlpha1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/getPointVectorTreeOfFunction")
public class GetPoints {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public FunktionAlsVektorSyntaxbaum getPoints(@QueryParam("functionBuffer") String functionBuffer) {
    	functionBuffer = "(x^2+(4*x^3-2*x+5))/(3*x^2+2*(2*x^4+3))";
    	RechnerLibrary rechnerLibrary = new RechnerLibrary();
    	FunktionAlsVektorSyntaxbaum baumGebrochenRational = new FunktionAlsVektorSyntaxbaum();
//		rechnerLibrary.splitFuntionBufferGebrochenRational(baumGebrochenRational, functionBuffer);
//		
//		FunktionAlsVektorSyntaxbaum gekuerzterBaum = baumGebrochenRational;
//		rechnerLibrary.kuerzeSyntaxbaumGebrochenRational(gekuerzterBaum);
//    	FunktionAlsVektorSyntaxbaum baum = new FunktionAlsVektorSyntaxbaum();
//    	baum.setInhalt('s');
//    	baumGebrochenRational.setRoot(baum);
//    	baumGebrochenRational.setIndex(1);
//    	baumGebrochenRational.setInhalt("test");
		/*FunktionAlsVektorSyntaxbaum gekuerzterBaum = baumGebrochenRational;
		rechnerLibrary.kuerzeSyntaxbaumGebrochenRational(gekuerzterBaum);
		rechnerLibrary.setSyntaxbaumGekuerzt(gekuerzterBaum);*/
        return baumGebrochenRational;
    }
}
