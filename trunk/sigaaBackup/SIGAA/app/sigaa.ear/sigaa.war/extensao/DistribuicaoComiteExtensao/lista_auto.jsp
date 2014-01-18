<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>
p {padding-right: 1cm;}
}
</style>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />
<f:view>
	<h2><ufrn:subSistema /> > Distribuir Propostas para Comit� de Extens�o</h2>

	<div class="descricaoOperacao">
		<b>Aten��o:</b><br/> 	
		 Somente poder�o ser distribu�das as A��es de Extens�o (Integradas, que apresentam linha de atua��o definida) 
		 que j� foram 'SUBMETIDAS' � PROEx ou as que est�o 'AGUARDANDO AVALIA��O'.<br/>		 
	</div>
	<br/>
	
    <%@include file="/extensao/barra_filtro_atividade.jsp"%>
	
	<c:if test="${not empty filtroAtividades.resultadosBusca}">

		<div class="infoAltRem">
			<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" rendered="#{acesso.extensao}"/><h:outputText rendered="#{acesso.extensao}" value=": Distribuir A��es Vinculadas"/>		    	    	
		    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar A��o
	    </div>
		<br/>
	
		<h:form id="formLista">
		
		
		<table class="listagem" width="100%">
		    <caption>A��es de Extens�o Encontradas (${fn:length(filtroAtividades.resultadosBusca)})</caption>
		 		<tbody>
					<tr>
						<td> 
					
							<t:dataTable id="listagem" value="#{filtroAtividades.resultadosBusca}" var="atividade" align="center" width="100%" styleClass="listagem tablesorter" rowClasses="linhaPar, linhaImpar">
	
								<t:column>
									<f:facet name="header"><h:selectBooleanCheckbox styleClass="chkSelecionaTodos" onclick="selecionarTodos();" /></f:facet>
									<h:selectBooleanCheckbox value="#{atividade.selecionado}" styleClass="todosChecks"/>
								</t:column>
	
							    <t:column style="width: 8%;">
	                                <f:facet name="header"><f:verbatim><p>C�digo</p></f:verbatim></f:facet>
	                                <h:outputText value="#{atividade.codigo}"/>                    
	                            </t:column>
							
								<t:column >
									<f:facet name="header"><f:verbatim>T�tulo</f:verbatim></f:facet>
									<h:outputText value="#{atividade.titulo}" />					
								</t:column>
								
								<t:column>
	                                <f:facet name="header"><f:verbatim>Situa��o</f:verbatim></f:facet>
	                                <h:outputText value="#{atividade.situacaoProjeto.descricao}" />                  
	                            </t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>�rea Tem�tica</f:verbatim></f:facet>
									<h:outputText value="#{atividade.areaTematicaPrincipal.descricao}" />
								</t:column>
								
								<t:column width="10%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>Financiamento</f:verbatim></f:facet>
									<h:graphicImage url="/img/extensao/bullet_square_green.png" title="Financiamento Interno" rendered="#{atividade.financiamentoInterno}"/>		
									<h:graphicImage url="/img/extensao/bullet_square_red.png" title="Financiamento Externo" rendered="#{atividade.financiamentoExterno}"/>
									<h:graphicImage url="/img/extensao/bullet_square_blue.png" title="Auto Financiamento" rendered="#{atividade.autoFinanciado}"/>
									<h:graphicImage url="/img/extensao/bullet_square_yellow.png" title="Conv�nio Funpec" rendered="#{atividade.convenioFunpec}"/>
								</t:column>										
							
								<t:column width="2%">
									<h:commandLink title="Distribuir A��es Vinculadas" action="#{ distribuicaoExtensao.distribuirAtividadesVinculadasComite }" 
									   rendered="#{acesso.extensao && atividade.permitidoIniciarAvaliacao}" id="listar_acoes_vinculadas">
									      <f:param name="id" value="#{atividade.id}"/>
									      <h:graphicImage url="/img/buscar.gif" />
									</h:commandLink>
								</t:column>
								
								<t:column width="2%">
									<h:commandLink  title="Visualizar A��o de Extens�o" action="#{ atividadeExtensao.view }" id="visualizar_acao">
									      <f:param name="id" value="#{atividade.id}"/>
									      <h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</t:column>
							
							</t:dataTable>
						</td>
					</tr>
				</tbody>
			
				<tfoot>
					<tr>						    	
						<td colspan="3" align="center">
							<h:commandButton value="Distribuir >>" action="#{ distribuicaoExtensao.distribuirOutraAtividadeComiteAuto }" id="btBuscar"/>
							<h:commandButton value="Cancelar" action="#{ distribuicaoExtensao.cancelar }" id="btCancelar" onclick="#{confirm}" />							
						</td>
				   </tr>
			   </tfoot>
			
	 	  </table>
		</h:form>
	</c:if>

</f:view>

<script type="text/javascript">
	function selecionarTodos(){
		var todosSelecionados = document.getElementsByClassName("chkSelecionaTodos")[0];
		var checks = document.getElementsByClassName("todosChecks");
		for (i=0; i<checks.length; i++){
		   checks[i].checked = todosSelecionados.checked;
		}
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>