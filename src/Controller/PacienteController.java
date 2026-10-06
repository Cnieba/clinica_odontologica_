package Controller;

import Entity.Domicilio;
import Entity.Paciente;
import Excepciones.ClinicaException;
import LogicaDeNegocio.ServicioPaciente;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class PacienteController {

    private ServicioPaciente serv;
    private Scanner teclado;

    public PacienteController(ServicioPaciente serv, Scanner teclado) {
        this.serv = serv;
        this.teclado = teclado;
    }

    public void menu() {
        int op;

        do {
            System.out.println("\n--- PACIENTES ---");
            System.out.println("1. Registrar");
            System.out.println("2. Listar (orden alfabético)");
            System.out.println("3. Buscar por DNI");
            System.out.println("4. Eliminar");
            System.out.println("5. Modificar");
            System.out.println("6. Buscar por apellido");
            System.out.println("0. Volver");

            op = leerEntero();

            switch (op) {
                case 1:
                    try {
                        System.out.print("Nombre: ");
                        String nombre = teclado.nextLine();

                        System.out.print("Apellido: ");
                        String apellido = teclado.nextLine();

                        System.out.print("DNI (7-8 dígitos): ");
                        String dni = teclado.nextLine();

                        System.out.print("Email: ");
                        String email = teclado.nextLine();

                        Paciente p = new Paciente(nombre, apellido, dni, email,
                                LocalDate.now(),
                                new Domicilio("Calle", "123", "Ciudad", "Provincia"));

                        serv.registrarPaciente(p);
                        System.out.println("Paciente registrado correctamente.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;

                case 2:
                    List<Paciente> lista = serv.listar();
                    if (lista.isEmpty()) System.out.println("No hay pacientes registrados.");
                    else lista.forEach(System.out::println);
                    break;

                case 3:
                    try {
                        System.out.print("DNI: ");
                        System.out.println(serv.buscar(teclado.nextLine()));
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;

                case 4:
                    try {
                        System.out.print("DNI: ");
                        serv.eliminarPaciente(teclado.nextLine());
                        System.out.println("Paciente eliminado.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;

                case 5:
                    try {
                        System.out.print("DNI: ");
                        String d = teclado.nextLine();

                        System.out.print("Nuevo nombre: ");
                        String n = teclado.nextLine();

                        System.out.print("Nuevo apellido: ");
                        String a = teclado.nextLine();

                        System.out.print("Nuevo email: ");
                        String e = teclado.nextLine();

                        serv.actualizarPaciente(d, n, a, e);
                        System.out.println("Paciente actualizado.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;

                case 6:
                    System.out.print("Fragmento de apellido: ");
                    List<Paciente> resultado = serv.buscarPorApellido(teclado.nextLine());
                    if (resultado.isEmpty()) System.out.println("No se encontraron pacientes.");
                    else resultado.forEach(System.out::println);
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
