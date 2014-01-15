<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 


<div class="menu">

    <div class="titulo">
        <h2>CADASTRO</h2>
    </div>
    <ul>
        <li>Servidor
            <ul>
                <li><html:link action="/geral/verServidorForm">Cadastrar</html:link></li>
                <li><html:link action="geral/listarServidores">Consultar</html:link></li>
			</ul>
		</li>
	</ul>		                
    <ul>
        <li>Pessoa
            <ul>
                <li><html:link action="/geral/verPessoaForm">Cadastrar</html:link></li>
                <li><html:link action="geral/listarPessoas">Consultar</html:link></li>
			</ul>
		</li>
	</ul>		                
    <ul>
        <li>Cidade
            <ul>
                <li>Cadastrar</li>
                <li>Consultar</li>
			</ul>
		</li>
	</ul>		                
    <ul>
        <li>Unidade Federativa
            <ul>
                <li>Cadastrar</li>
                <li>Consultar</li>
			</ul>
		</li>
	</ul>		                
    <ul>
        <li> ...
            <ul>
                <li>Cadastrar</li>
                <li>Consultar</li>
			</ul>
		</li>
	</ul>		                

</div>