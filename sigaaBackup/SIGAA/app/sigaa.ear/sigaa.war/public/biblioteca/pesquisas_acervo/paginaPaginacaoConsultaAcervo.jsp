
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="/tags/ajax" prefix="ajax"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>

<style>	
	
	/** classe para o botão de paginação quando não está selecionado  **/
	.button_pagination {
	    background: -moz-linear-gradient(center top , #FFFFFF, #EFEFEF) repeat scroll 0 0 #F6F6F6;
	    border: 1px solid #CCCCCC;
	    border-radius: 3px 3px 3px 3px;
	    height: 2.0833em;
	    overflow: visible;
	    padding: 0 0.5em;
	    vertical-align: middle;
	    white-space: nowrap;
	    font-weight:  bolder;
	    font-size: 12px; 
	}
	
	
</style>

<a4j:keepAlive beanName="pesquisarAcervoPaginadoBiblioteca"></a4j:keepAlive>

<%-- Visualização dos links para realizar a paginação das consultas do acervo  --%>
    
<div style="width: 100%; text-align: center; margin-top: 10px; margin-bottom: 20px;">
		
	<h:commandLink id="botaoPrimeiraPagina" value="<<" actionListener="#{pesquisarAcervoPaginadoBiblioteca.atualizaResultadosPaginacao}" disabled="#{pesquisarAcervoPaginadoBiblioteca.paginaAtual == 1}"
			rendered="#{pesquisarAcervoPaginadoBiblioteca.quantidadePaginas > 1}"
			styleClass="button_pagination">
			<f:param name="_numero_pagina_atual" value="1"/>
	</h:commandLink> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
	<h:commandLink id="botaoPaginaAnterior" value="<" actionListener="#{pesquisarAcervoPaginadoBiblioteca.atualizaResultadosPaginacao}" disabled="#{pesquisarAcervoPaginadoBiblioteca.paginaAtual == 1}"
				rendered="#{pesquisarAcervoPaginadoBiblioteca.quantidadePaginas > 1}"
				styleClass="button_pagination">
			<f:param name="_numero_pagina_atual" value="#{pesquisarAcervoPaginadoBiblioteca.paginaAtual -1}"/>
	</h:commandLink>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

	<c:forEach var="pagina" items="#{pesquisarAcervoPaginadoBiblioteca.listaPaginasVisiveis}" >
		<h:commandLink id="botaoPagina" value="#{pagina}" actionListener="#{pesquisarAcervoPaginadoBiblioteca.atualizaResultadosPaginacao}" disabled="#{pesquisarAcervoPaginadoBiblioteca.paginaAtual == pagina}"
				styleClass="button_pagination">
			<f:param name="_numero_pagina_atual" value="#{pagina}"/>
		</h:commandLink>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</c:forEach>
	
	<h:commandLink id="botaoProximaPagina" value=">" actionListener="#{pesquisarAcervoPaginadoBiblioteca.atualizaResultadosPaginacao}" disabled="#{pesquisarAcervoPaginadoBiblioteca.paginaAtual == pesquisarAcervoPaginadoBiblioteca.quantidadePaginas}"
			rendered="#{pesquisarAcervoPaginadoBiblioteca.quantidadePaginas > 1}"
			styleClass="button_pagination">
			<f:param name="_numero_pagina_atual" value="#{pesquisarAcervoPaginadoBiblioteca.paginaAtual +1}"/>
	</h:commandLink> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
	<h:commandLink id="botaoUltimaPagina" value=">>" actionListener="#{pesquisarAcervoPaginadoBiblioteca.atualizaResultadosPaginacao}" disabled="#{pesquisarAcervoPaginadoBiblioteca.paginaAtual == pesquisarAcervoPaginadoBiblioteca.quantidadePaginas}"
			rendered="#{pesquisarAcervoPaginadoBiblioteca.quantidadePaginas > 1}"
			styleClass="button_pagination">
			<f:param name="_numero_pagina_atual" value="#{pesquisarAcervoPaginadoBiblioteca.quantidadePaginas}" />
	</h:commandLink>	
	
</div>