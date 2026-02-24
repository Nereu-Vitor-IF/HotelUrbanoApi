async function carregarQuartos() {
    console.log("Iniciando busca de dados...");
    const tabela = document.getElementById("corpo-tabela");

    try {
        // Usamos 127.0.0.1 para evitar conflito de DNS do Windows
        const resposta = await fetch("http://127.0.0.1:8080/quartos");
        const dados = await resposta.json();
        
        console.log("Dados que chegaram:", dados);

        tabela.innerHTML = ""; // Limpa a tabela

        if (dados.length === 0) {
            tabela.innerHTML = "<tr><td colspan='6' class='text-center'>Nenhum quarto encontrado no banco.</td></tr>";
            return;
        }

        dados.forEach(q => {
            // Criamos a linha garantindo que se o campo for nulo, não quebre o código
            let linha = `
                <tr>
                    <td><img src="${q.urlImagem || ''}" width="60" onerror="this.src='https://via.placeholder.com/60'"></td>
                    <td>${q.numeroQuarto || 'S/N'}</td>
                    <td>${q.tipoQuarto || 'N/A'}</td>
                    <td>${q.descricao || ''}</td>
                    <td>R$ ${q.precoDiaria || '0'}</td>
                    <td>${q.disponivel ? 'Livre' : 'Ocupado'}</td>
                </tr>`;
            tabela.innerHTML += linha;
        });
        
        console.log("Tabela preenchida!");

    } catch (erro) {
        console.error("ERRO NO FETCH:", erro);
        tabela.innerHTML = "<tr><td colspan='6' class='text-center text-danger'>Erro de conexão com a API</td></tr>";
    }
}

// Executa
carregarQuartos();