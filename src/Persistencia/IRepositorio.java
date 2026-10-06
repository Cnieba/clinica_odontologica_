package Persistencia;

import java.util.List;

public interface IRepositorio<T,ID> {
    void guardar(T t);
    T buscarPorId(ID id);
    List<T> listarTodos();
    void eliminar(ID id);
}
