package br.com.fiap.veiculo.infra.database.entity.veiculo;

public enum StatusVeiculo {
    DISPONIVEL("DISPONIVEL"),
    RESERVADO("RESERVADO"),
    VENDIDO("VENDIDO");

    private final String name;

    // Construtor
    StatusVeiculo(String name) {
        this.name = name;
    }

    // Getter
    public String getName() {
        return name;
    }
}