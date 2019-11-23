package unsl.controllers;
import java.util.HashMap;
import java.util.List;

import org.h2.message.DbException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsl.entities.ResponseError;
import unsl.entities.User;
import unsl.services.UserService;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    //OBTIENE UNA LISTA CON TODOS LOS CLIENTES
    @GetMapping(value = "/clientes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
       return userService.getAll();
    }

    //OBTIENE LOS DATOS DEL CLIENTE CON id ESPECIFICADO
    @GetMapping(value = "/clientes/{id}")
    @ResponseBody
    public Object getUser(@PathVariable("id") Long id) {
        User user = userService.getUser(id);
        if ( user == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cliente con id %d no encontrado.", id)), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    //OBTIENE LOS DATOS DEL CLIENTE CON dni ESPECIFICADO
    @GetMapping(value = "/clientes/busqueda")
    @ResponseBody
    public Object searchUser(@RequestParam("dni") Long dni) {
        User user = userService.findByDni(dni);
        if ( user == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cliente con id %d no encontrado.", dni)), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    //INSERTA UN NUEVO CLIENTE
    @PostMapping(value = "/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Object createUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            if( savedUser == null) {
                return new ResponseEntity(new ResponseError(400, String.format("Nombre, apellido y dni del cliente requeridos.")), HttpStatus.NOT_FOUND);
            }
            else{
                HashMap<String, Long> map = new HashMap<>();
                long value = savedUser.getId();
                map.put("id", value);
                return map;
            }
        }catch(Exception e){
            return new ResponseEntity(new ResponseError(500, String.format("Ya existe un cliente con ese dni.")), HttpStatus.NOT_FOUND);
        }
    }

    //MODIFICA LOS DATOS (dni, nombre y apellido) DEL CLIENTE CON id ESPECIFICADO
    @PutMapping(value = "/clientes/{id}")
    @ResponseBody
    public Object updateUser(@PathVariable("id") Long id, @RequestBody User User) {
        User user = userService.updateUser(id,User);
        if ( user == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cliente con id %d no encontrado.", id)), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    //REALIZA LA BAJA LOGICA DEL CLIENTE Y TODAS SUS CUENTAS DEL CLIENTE CON id ESPECIFICADO
    @DeleteMapping(value = "/clientes/{id}")
    @ResponseBody
    public Object logicDeleteUser(@PathVariable("id") Long id) {
        User user = userService.logicDeleteUser(id);
        if ( user == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cliente con id %d no encontrado.", id)), HttpStatus.NOT_FOUND);
        }
        return user;
    }

}

