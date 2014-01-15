<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="h"    uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="f"    uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<div class="descricaoOperacao">
    Cadastro de informa��es que s�o necess�rias para o sistema funcionar.
</div>

<ul>
	
	<li>Configura��es do Sistema
		<ul>
			 <li> <h:commandLink action="#{configuraParametrosGeraisBibliotecaMBean.iniciar}" onclick="setAba('cadastros')" value="Configura��es Gerais" /> </li>
			 <li> <h:commandLink action="#{configuraParametrosProcessosTecnicosMBean.iniciar}" onclick="setAba('cadastros')" value="Configura��es de Processos T�cnicos" /> </li>
			 <li> <h:commandLink action="#{configuraParametrosCirculacaoMBean.iniciar}" onclick="setAba('cadastros')" value="Configura��es de Circula��o" /> </li>
			 <li> <h:commandLink action="#{configuraParametrosInformacaoReferenciaMBean.iniciar}" onclick="setAba('cadastros')" value="Configura��es de Informa��o e Refer�ncia" /> </li>
		</ul>
	</li>
	
	<li>Classifica��es Bibliogr�ficas
		<ul>
			<li><h:commandLink value="Configurar Classifica��es Utilizadas" action="#{classificacaoBibliograficaMBean.iniciar}"  onclick="setAba('cadastros')"  /></li>
			<li> <h:commandLink value="Configurar Relacionamento com as Bibliotecas"  action="#{relacionaClassificacaoBibliograficaBibliotecasMBean.iniciar}" onclick="setAba('cadastros')"  /></li>
			<li> <h:commandLink value="Configurar Relacionamento com as �reas do CNPq"  action="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.iniciar}" onclick="setAba('cadastros')"  /></li>
			<li> <h:commandLink value="Configurar Informa��es das �reas do CNPq"  action="#{configuraInformacoesAreasCNPqBibliotecaMBean.iniciar}" onclick="setAba('cadastros')"  /></li>
		</ul>		
	</li>
	
	
	<li> <a id="linkVisualizaPapeis" href="/sigaa/biblioteca/listaPapeisEOperacoesModuloBiblioteca.jsf" onclick="setAba('cadastros')">Pap�is do Sistema </a> </li>
	
	
	
	<li>Bibliotecas
		<ul>
			<li><h:commandLink action="#{bibliotecaMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Bibliotecas" /></li>
		</ul>
	</li>

	<li>Cole��es
		<ul>
			<li><h:commandLink action="#{colecaoMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Cole��es" /></li>
		</ul>
	</li>
	
	<li>Situa��es dos Materiais Informacionais
		<ul>
			<li><h:commandLink action="#{situacaoMaterialInformacionalMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Situa��es" /></li>
		</ul>
	</li>
	
	<li>Status dos Materiais Informacionais
		<ul>
			<li><h:commandLink action="#{statusMaterialInformacionalMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Status" /></li>
		</ul>
	</li>

	<li>Tipos de Empr�stimos
		<ul>
			<li><h:commandLink action="#{tipoEmprestimo.listar}" onclick="setAba('cadastros')" value="Gerenciar Tipos de Empr�stimos" /></li>
		</ul>
	</li>

	<li>Pol�tica de Empr�stimos
		<ul>
			<li><h:commandLink action="#{politicaEmprestimoMBean.iniciaAlteracaoPoliticas}" onclick="setAba('cadastros')" value="Gerenciar Pol�ticas de Empr�stimos" /></li>
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
	
	<li>Conv�nios
		<ul>
			<li><h:commandLink action="#{convenioBiblioteca.listar}" onclick="setAba('cadastros')" value="Gerenciar Conv�nios" /></li>
		</ul>
	</li>

	<li><h:commandLink action="#{ associacaoCursoBibliotecaMBean.escolherBiblioteca }" onclick="setAba('cadastros')" value="Associar Cursos �s Bibliotecas Setoriais" id="listarCursosAssociados"/></li>
	
	
	<li>Tipos de Documentos Normaliza��o e Cataloga��o
		<ul>
			<li><h:commandLink action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.listar}" onclick="setAba('cadastros')" value="Gerenciar Tipos de Documentos" /></li>
		</ul>	
	</li>

	
		
	<li><h:commandLink action="#{emailsNotificacaoServicosBibliotecaMBean.listar}" onclick="setAba('cadastros')" value="E-mails de Notifica��es das Bibliotecas" /></li>
		
	
	
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
				<li><h:commandLink value="Cadastrar Consultas Locais Usando Leitor �tico"
						id="linkCadastrarConsultasLocaisUsandoLeitorOtico"
						action="#{registroConsultasDiariaisMateriaisLeitorMBean.iniciarRegistroConsulta}"
						onclick="setAba('cadastros')"/></li>
			</ul>
		</li>
	</ufrn:checkRole>

	
		
	<li>Invent�rios do Acervo
		<ul>
			<li><h:commandLink action="#{inventarioAcervoBibliotecaMBean.iniciarPesquisaInventarios}" onclick="setAba('cadastros')" value="Gerenciar Invent�rios" /></li>
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