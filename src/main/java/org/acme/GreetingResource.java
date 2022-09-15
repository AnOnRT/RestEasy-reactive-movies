package org.acme;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/movies")
@Tag(name = "Movie Resource", description = "Movie Rest API")
public class GreetingResource {

    public static List<Movie> movies = new ArrayList<>();
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "getMovies",
            summary = "Get Movies",
            description = "Get the list of movies"
    )
    @APIResponse(
        responseCode = "200",
        description = "Operation Completed",
        content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getMovie(){
        return Response.ok(movies).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/size")
    @Operation(
            operationId = "getMovieCount",
            summary = "Quantity of movies",
            description = "Get the quantity of the movies on the list"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Completed",
            content = @Content(mediaType = MediaType.TEXT_PLAIN)
    )
    public Integer countMovies(){
        return movies.size();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON) //We reproduce our list
    @Consumes(MediaType.APPLICATION_JSON) //Consumes new movie
    @Operation(
            operationId = "addMovie",
            summary = "Add movie",
            description = "Add new movie to the list"
    )
    @APIResponse(
            responseCode = "201",
            description = "Operation Completed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response createMovie(
            @RequestBody(
                    description = "Movie to be added",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Movie.class))
                )
                Movie newMovie){
        movies.add(newMovie);
        return Response.status(Response.Status.CREATED).entity(movies).build();
    }

    @PUT
    @Path("{id}/{title}")
    @Produces(MediaType.APPLICATION_JSON) //We reproduce our list
    @Consumes(MediaType.APPLICATION_JSON) //Consumes new movie
    @Operation(
            operationId = "updateMovie",
            summary = "Update movie",
            description = "Update movie that is currently on the list"
    )
    @APIResponse(
            responseCode = "200",
            description = "Movie updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response updateMovie(
            @Parameter(
                    description =  "Movie ID",
                    required = true
            )
            @PathParam("id") Long id,
            @Parameter(
                    description =  "Movie title ",
                    required = true
            )
            @PathParam("title") String title){

        movies = movies.stream().map(movie -> {
            if(movie.getId().equals(id)){
                movie.setTitle(title);
            }
            return movie;
        }).collect(Collectors.toList());

        
        return Response.ok(movies).build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "deleteMovie",
            summary = "Delete movie",
            description = "Delete movie that is currently on the list"
    )
    @APIResponse(
            responseCode = "204",
            description = "Movie deleted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "404",
            description = "Bad request, movie does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response deleteMovie(@PathParam("id") Long id){
        
        Optional<Movie> movieToDelete = movies.stream().filter(movie -> movie.getId().equals(id))
                .findFirst();
        boolean removed = false;
        if(movieToDelete.isPresent()){
            removed = movies.remove(movieToDelete.get());
        }
        
        return removed ? Response.noContent().build()
                 : Response.status(Response.Status.BAD_REQUEST).build();
    }
}