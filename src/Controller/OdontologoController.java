package Controller;

import Entity.*;
import Excepciones.ClinicaException;
import LogicaDeNegocio.ServicioOdonto;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class OdontologoController {

    private ServicioOdonto serv;
    private Scanner teclado;

    public OdontologoController(ServicioOdonto serv, Scanner teclado) {
        this.serv = serv;
        this.teclado = teclado;
    }

    public void menu() {
        int op;

        do {
            System.out.println("\n--- ODONTOLOGOS ---");
            System.out.println("1. Registrar");
            System.out.println("2. Listar (orden alfabético)");
            System.out.println("3. Eliminar");
            System.out.println("0. Volver");

            op = leerEntero();

            switch (op) {
                case 1:
                    try {
                        System.out.println("Tipo:");
                        System.out.println("1. Endodoncista");
                        System.out.println("2. Ortodoncista");
                        System.out.println("3. Periodoncista");
                        System.out.println("4. Odontopediatra");

                        int tipo = leerEntero();

                        System.out.print("Nombre: ");
                        String nombre = teclado.nextLine();

                        System.out.print("Apellido: ");
                        String apellido = teclado.nextLine();

                        System.out.print("Matrícula: ");
                        String mat = teclado.nextLine();

                        Odontologo o;
                        switch (tipo) {
                            case 1: o = new Endodoncista(nombre, apellido, mat); break;
                            case 2: o = new Ortodoncista(nombre, apellido, mat); break;
                            case 3: o = new Periodoncista(nombre, apellido, mat); break;
                            case 4: o = new Odontopediatra(nombre, apellido, mat); break;
                            default: o = new Odontologo(nombre, apellido, mat);
                        }

                        serv.registrarOdontologo(o);
                        System.out.println("Odontólogo registrado correctamente.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;

                case 2:
                    List<Odontologo> lista = serv.listar();
                    if (lista.isEmpty()) System.out.println("No hay odontólogos registrados.");
                    else lista.forEach(System.out::println);
                    break;

                case 3:
                    try {
                        System.out.print("Matrícula: ");
                        serv.eliminar(teclado.nextLine());
                        System.out.println("Odontólogo eliminado.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;
            }

        } while (op != 0);
    }

    private int leerEntero() {
        try {
            int val = teclado.nextInt();
            teclado.nextLine();
            return val;
        } catch (InputMismatchException e) {
            teclado.nextLine();
            System.out.println("Ingrese un número válido.");
            return -1;
        }
    }
}
