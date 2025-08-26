# Módulo de Veículos – Regras de Negócio

---

## 1. Cadastro de Veículo

**Use Case:** `CriarVeiculoUseCase`  
**Validações:**
- Marca, modelo e cor não podem ser nulos ou vazios.
- Ano entre 1900 e o ano atual.
- Preço e quilometragem não podem ser negativos.
- Status padrão: `DISPONIVEL` se não informado.

---

## 2. Atualização de Veículo

**Use Case:** `AlterarVeiculoUseCase` (PATCH)  
**Validações:**
- ID obrigatório e veículo deve existir.
- Apenas campos informados são atualizados.
- Status deve ser válido (`DISPONIVEL`, `RESERVADO`, `VENDIDO`).
- Data de atualização atualizada automaticamente.

---

## 3. Deleção de Veículo

**Use Case:** `DeletarVeiculoUseCase`  
**Validações:**
- ID obrigatório e veículo deve existir.
- (Opcional) Impedir deleção de veículos vendidos.

---

## 4. Obtenção de Veículo por ID

**Use Case:** `ObterVeiculoPorIdUseCase`  
**Validações:**
- ID obrigatório.
- Se não existir, lança `VeiculoNaoEncontradoException`.

---

## 5. Listagem de Veículos por Status

**Use Case:** `ObterVeiculosPorStatusUseCase`  
**Validações:**
- Status obrigatório e válido (`DISPONIVEL`, `RESERVADO`, `VENDIDO`).
- Retorna lista ordenada por preço (mais barato → mais caro).

---

## 6. Listagem de Todos os Veículos

**Use Case:** `ObterVeiculoUseCase`  
**Validações:**
- Sempre retorna lista (mesmo que vazia).

---

## 7. Enum `StatusVeiculo`

- `DISPONIVEL` → à venda
- `RESERVADO` → reservado
- `VENDIDO` → vendido

