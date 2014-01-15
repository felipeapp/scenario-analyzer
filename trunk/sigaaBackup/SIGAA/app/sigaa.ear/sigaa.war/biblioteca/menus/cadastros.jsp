<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="h"    uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="f"    uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<div class="descricaoOperacao">
    Cadastro de informações que são necessárias para o sistema funcionar.
</div>

<ul>
	
	<li>Configurações do Sistema
		<ul>
			 <li> <h:commandLink action="#{configuraParametrosGeraisBibliotecaMBean.iniciar}" onclick="setAba('cadastros')" value="Configurações Gerais" /> </li>
			 <li> <h:commandLink action="#{configuraParametrosProcessosTecnicosMBean.iniciar}" onclick="setAba('cadastros')" value="Configurações de Processos Técnicos" /> </li>
			 <li> <h:commandLink action="#{configuraParametrosCirculacaoMBean.iniciar}" onclick="setAba('cadastros')" value="Configurações de Circulação" /> </li>
			 <li> <h:commandLink action="#{configuraParametrosInformacaoReferenciaMBean.iniciar}" onclick="setAba('cadastros')" value="Configurações de Informação e Referência" /> </li>
		</ul>
	</li>
	
	<li>Classificações Bibliográficas
		<ul>
			<li><h:commandLink value="Configurar Classificações Utilizadas" action="#{classificacaoBibliograficaMBean.iniciar}"  onclick="setAba('cadastros')"  /></li>
			<li> <h:commandLink value="Configurar Relacionamento com as Bibliotecas"  action="#{relacionaClassificacaoBibliograficaBibliotecasMBean.iniciar}" onclick="setAba('cadastros')"  /></li>
			<li> <h:commandLink value="Configurar Relacionamento com as Áreas do CNPq"  action="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.iniciar}" onclick="setAba('cadastros')"  /></li>
			<li> <h:commandLink value="Configurar Informações das Áreas do CNPq"  action="#{configuraInformacoesAreasCNPqBibliotecaMBean.iniciar}" onclick="setAba('cadastros')"  /></li>
		</ul>		
	</li>
	
	
	<li> <a id="linkVisualizaPapeis" href="/sigaa/biblioteca/listaPapeisEOperacoesModuloBiblioteca.jsf" onclick="setAba('cadastros')">Papéis do Sistema </a> </li>
	
	
	
	<li>Bibliotecas
		<ul>
			<li><h:commandLink action="#{bibliotecaMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Bibliotecas" /></li>
		</ul>
	</li>

	<li>Coleções
		<ul>
			<li><h:commandLink action="#{colecaoMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Coleções" /></li>
		</ul>
	</li>
	
	<li>Situações dos Materiais Informacionais
		<ul>
			<li><h:commandLink action="#{situacaoMaterialInformacionalMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Situações" /></li>
		</ul>
	</li>
	
	<li>Status dos Materiais Informacionais
		<ul>
			<li><h:commandLink action="#{statusMaterialInformacionalMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Status" /></li>
		</ul>
	</li>

	<li>Tipos de Empréstimos
		<ul>
			<li><h:commandLink action="#{tipoEmprestimo.listar}" onclick="setAba('cadastros')" value="Gerenciar Tipos de Empréstimos" /></li>
		</ul>
	</li>

	<li>Política de Empréstimos
		<ul>
			<li><h:commandLink action="#{politicaEmprestimoMBean.iniciaAlteracaoPoliticas}" onclick="setAba('cadastros')" value="Gerenciar Políticas de Empréstimos" /></li>
		</ul>
	</li>
	

	<li>Tipos de Materiais
		<ul>
			<li><h:commandLink action="#{tipoMaterialMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Tipos de Materiais" /></li> 
		</ul>
	</li>
	
	<li>Formas de Documento
		<ul>
			<li><h:commandLink action="#{formaDocumentoMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Formas de Documento" /></li> 
		</ul>
	</li>
	
	<li>Convênios
		<ul>
			<li><h:commandLink action="#{convenioBiblioteca.listar}" onclick="setAba('cadastros')" value="Gerenciar Convênios" /></li>
		</ul>
	</li>

	<li><h:commandLink action="#{ associacaoCursoBibliotecaMBean.escolherBiblioteca }" onclick="setAba('cadastros')" value="Associar Cursos às Bibliotecas Setoriais" id="listarCursosAssociados"/></li>
	
	
	<li>Tipos de Documentos Normalização e Catalogação
		<ul>
			<li><h:commandLink action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Tipos de Documentos" /></li>
		</ul>	
	</li>

	
		
	<li><h:commandLink action="#{emailsNotificacaoServicosBibliotecaMBean.listar}" onclick="setAba('cadastros')" value="E-mails de Notificações das Bibliotecas" /></li>
		
	
	
	<ufrn:checkRole papeis="<%= new int[] {
			SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
			SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
			SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
			SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
			SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Consultas Locais
			<ul>
				<li><h:commandLink value="Cadastrar Consultas Locais"
						id="linkCadastrarConsultasLocais"
						action="#{registraConsultasDiariasMateriaisMBean.iniciarRegistroConsultasDiarias}"
						onclick="setAba('cadastros')"/></li>
				<li><h:commandLink value="Cadastrar Consultas Locais Usando Leitor Ótico"
						id="linkCadastrarConsultasLocaisUsandoLeitorOtico"
						action="#{registroConsultasDiariaisMateriaisLeitorMBean.iniciarRegistroConsulta}"
						onclick="setAba('cadastros')"/></li>
			</ul>
		</li>
	</ufrn:checkRole>

	
		
	<li>Inventários do Acervo
		<ul>
			<li><h:commandLink action="#{inventarioAcervoBibliotecaMBean.iniciarPesquisaInventarios}" onclick="setAba('cadastros')" value="Gerenciar Inventários" /></li>
		</ul>
		<ul>
			<li><h:commandLink action="#{inventarioAcervoBibliotecaMBean.iniciarRemocaoRegistro}" onclick="setAba('cadastros')" value="Remover Registros" /></li>
		</ul>
	</li>
							
	
	
	
	<li>Campos MARC
		<ul>
			<li><h:commandLink action="#{etiquetaMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Campo Local" /></li>
		</ul>	
	</li>

	

</ul>