package Controller;

import Entity.*;
import Excepciones.ClinicaException;
import LogicaDeNegocio.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class TurnoController {

    private ServicioTurno servTur;
    private ServicioPaciente servPac;
    private ServicioOdonto servOdo;
    private Scanner teclado;

    public TurnoController(ServicioTurno t, ServicioPaciente p, ServicioOdonto o, Scanner teclado) {
        this.servTur = t;
        this.servPac = p;
        this.servOdo = o;
        this.teclado = teclado;
    }

    public void menu() {
        int op;

        do {
            System.out.println("\n--- TURNOS ---");
            System.out.println("1. Crear");
            System.out.println("2. Listar (ordenados por fecha/hora)");
            System.out.println("3. Eliminar");
            System.out.println("4. Modificar");
            System.out.println("5. Buscar por rango de fechas");
            System.out.println("6. Buscar por odontólogo");
            System.out.println("7. Buscar por paciente");
            System.out.println("0. Volver");

            op = leerEntero();

            switch (op) {
                case 1:
                    try {
                        System.out.print("DNI paciente: ");
                        Paciente p = servPac.buscar(teclado.nextLine());

                        System.out.print("Matrícula odontólogo: ");
                        Odontologo o = servOdo.buscar(teclado.nextLine());

                        System.out.print("Fecha (YYYY-MM-DD): ");
                        LocalDate fecha = LocalDate.parse(teclado.nextLine());

                        System.out.print("Hora (HH:MM): ");
                        LocalTime hora = LocalTime.parse(teclado.nextLine());

                        Turno t = new Turno(p, o, fecha, hora, EstadoTurno.PENDIENTE);
                        servTur.crearTurno(t);
                        System.out.println("Turno creado correctamente.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    } catch (DateTimeParseException e) {
                        System.out.println("Formato de fecha/hora inválido. Use YYYY-MM-DD y HH:MM.");
                    }
                    break;

                case 2:
                    List<Turno> lista = servTur.listar();
                    if (lista.isEmpty()) System.out.println("No hay turnos registrados.");
                    else lista.forEach(System.out::println);
                    break;

                case 3:
                    try {
                        System.out.print("ID del turno: ");
                        servTur.eliminar(leerEntero());
                        System.out.println("Turno eliminado.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;

                case 4:
                    try {
                        System.out.print("ID del turno: ");
                        int id = leerEntero();

                        System.out.print("Nueva fecha (YYYY-MM-DD): ");
                        LocalDate f = LocalDate.parse(teclado.nextLine());

                        System.out.print("Nueva hora (HH:MM): ");
                        LocalTime h = LocalTime.parse(teclado.nextLine());

                        servTur.actualizarTurno(id, f, h);
                        System.out.println("Turno actualizado.");
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    } catch (DateTimeParseException e) {
                        System.out.println("Formato de fecha/hora inválido. Use YYYY-MM-DD y HH:MM.");
                    }
                    break;

                case 5:
                    try {
                        System.out.print("Fecha desde (YYYY-MM-DD): ");
                        LocalDate desde = LocalDate.parse(teclado.nextLine());

                        System.out.print("Fecha hasta (YYYY-MM-DD): ");
                        LocalDate hasta = LocalDate.parse(teclado.nextLine());

                        List<Turno> rango = servTur.buscarPorRangoFechas(desde, hasta);
                        if (rango.isEmpty()) System.out.println("No hay turnos en ese rango.");
                        else rango.forEach(System.out::println);
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    } catch (DateTimeParseException e) {
                        System.out.println("Formato de fecha inválido. Use YYYY-MM-DD.");
                    }
                    break;

                case 6:
                    try {
                        System.out.print("Matrícula del odontólogo: ");
                        List<Turno> porOdo = servTur.buscarPorOdontologo(teclado.nextLine());
                        if (porOdo.isEmpty()) System.out.println("No hay turnos para ese odontólogo.");
                        else porOdo.forEach(System.out::println);
                    } catch (ClinicaException e) {
                        System.out.println("Error: " + e);
                    }
                    break;

                case 7:
                    try {
                        System.out.print("DNI del paciente: ");
                        List<Turno> porPac = servTur.buscarPorPaciente(teclado.nextLine());
                        if (porPac.isEmpty()) System.out.println("No hay turnos para ese paciente.");
                        else porPac.forEach(System.out::println);
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
