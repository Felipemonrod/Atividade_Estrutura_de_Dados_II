public class Trabalho {
    private int id_aluno;
    private String nome_arquivo;
    private int numero_pagina;

    public Trabalho(int id_aluno, String nome_arquivo, int numero_pagina) {
        this.id_aluno = id_aluno;
        this.nome_arquivo = nome_arquivo;
        this.numero_pagina = numero_pagina;
    }

    public void showAll(){
        System.out.println("Id "+id_aluno+"/ Nome do arquivo"+nome_arquivo+"Numero de pagina: "+numero_pagina);
    }

    public int getId_aluno() {
        return this.id_aluno;
    }

    public void setId_aluno(int id_aluno) {
        this.id_aluno = id_aluno;
    }

    public String getNome_arquivo() {
        return this.nome_arquivo;
    }

    public void setNome_arquivo(String nome_arquivo) {
        this.nome_arquivo = nome_arquivo;
    }

    public int getNumero_pagina() {
        return this.numero_pagina;
    }

    public void setNumero_pagina(int numero_pagina) {
        this.numero_pagina = numero_pagina;
    }


    
}
