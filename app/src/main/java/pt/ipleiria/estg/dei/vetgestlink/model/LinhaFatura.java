package pt.ipleiria.estg.dei.vetgestlink.model;


public class LinhaFatura {

    private int id;
    private float total;
    private int quantidade;
    private boolean vendidoEmConsulta;
    private int faturasId;
    private Integer medicamentosId;
    private Integer marcacoesId;
    private boolean eliminado;

    //Constructor

    public LinhaFatura(int id, float total, int quantidade, boolean vendidoEmConsulta,
                       int faturasId, Integer medicamentosId, Integer marcacoesId, boolean eliminado) {

        this.id = id;
        this.total = total;
        this.quantidade = quantidade;
        this.vendidoEmConsulta = vendidoEmConsulta;
        this.faturasId = faturasId;
        this.medicamentosId = medicamentosId;
        this.marcacoesId = marcacoesId;
        this.eliminado = eliminado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getTotal() { return total; }
    public void setTotal(float total) { this.total = total; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public boolean isVendidoEmConsulta() { return vendidoEmConsulta; }
    public void setVendidoEmConsulta(boolean vendidoEmConsulta) { this.vendidoEmConsulta = vendidoEmConsulta; }

    public int getFaturasId() { return faturasId; }
    public void setFaturasId(int faturasId) { this.faturasId = faturasId; }

    public Integer getMedicamentosId() { return medicamentosId; }
    public void setMedicamentosId(Integer medicamentosId) { this.medicamentosId = medicamentosId; }

    public Integer getMarcacoesId() { return marcacoesId; }
    public void setMarcacoesId(Integer marcacoesId) { this.marcacoesId = marcacoesId; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
}
