<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

<h2><ufrn:subSistema /> &gt; Homologação de Bolsas de Iniciação à Pesquisa</h2>

<h:form id="form">

	<div class="descricaoOperacao">
		<p><strong>Caro Gestor,</strong></p>
		<p>Esta operação lista os alunos indicados para modalidades de bolsas pagas pela própria instituição, 
		permitindo que sejam incluídas solicitações de bolsas no SIPAC automaticamente. Selecione uma modalidade de bolsa se desejar refinar a consulta.</p>
		<p>A listagem apresenta apenas alunos que ainda não foram homologados através desta operação.</p> 
	</div>

	<table class="formulario" width="50%">
		<caption>Busca por Indicações de Bolsistas</caption>
		<tbody>

		    <tr>					
		    	<th width="30%"> Modalidade da Bolsa:</th>
		    	<td>	    	
		    	 <h:selectOneMenu id="buscaTipoBolsa" value="#{homologacaoBolsistaPesquisaBean.tipoBolsa.id}">
					<f:selectItem itemLabel="-- TODAS --" itemValue="0"/>
		    	 	<f:selectItems value="#{tipoBolsaPesquisa.pagasSipacCombo}" />
		    	 </h:selectOneMenu>
		    	 </td>
		    </tr>
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
				<h:commandButton id="btBucar" value="Buscar" action="#{ homologacaoBolsistaPesquisaBean.buscar }"/>
				<h:commandButton id="btCancelar" value="Cancelar" action="#{ homologacaoBolsistaPesquisaBean.cancelar }" onclick="#{confirm}" immediate="true"/>
		    	</td>
		    </tr>
		</tfoot>
	</table>
	
	<br/>
	<c:set var="discentesBolsistasSigaa" value="#{homologacaoBolsistaPesquisaBean.discentesBolsistasSigaa}"/>

	<c:if test="${not empty discentesBolsistasSigaa}">
		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Resumo da Indicação/Substituição
		    <h:graphicImage url="/img/pesquisa/remover_bolsista.gif" style="overflow: visible;"/>: Ignorar Indicação de Bolsista
		</div>
		 <table class="listagem" width="100%">
		    <caption>Discentes Encontrados (${ fn:length(discentesBolsistasSigaa) })</caption>

		      <thead>
		      	<tr>
					<th><h:selectBooleanCheckbox value="true" onclick="checkAll(this)"/></th>		      	
		        	<th>Matrícula</th>
		        	<th>Nome</th>
		        	<th>Curso</th>
		        	<th>Cód. Projeto</th>
		        	<c:if test="${homologacaoBolsistaPesquisaBean.tipoBolsa.id == 0}">
			        	<th>Modalidade</th>
		        	</c:if>
		        	<th style="text-align: center;">Data da Indicação</th>
		        	<th></th>
		        	<th></th>
		        </tr>
		 	</thead>
		 	<tbody>

	       		<c:forEach items="#{homologacaoBolsistaPesquisaBean.discentesBolsistasSigaa}" var="de" varStatus="status">				
					
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                   <td><h:selectBooleanCheckbox value="#{de.selecionado}" styleClass="check"/></td> 
	                   <td><h:outputText value="#{de.discente.matricula}"/></td>
	                   <td><h:outputText value="#{de.discente.nome}"/></td>
	                   <td><h:outputText value="#{de.discente.curso.descricao}"/></td>
	                   <td><h:outputText value="#{de.planoTrabalho.projetoPesquisa.codigo}" /></td>
	                   <c:if test="${homologacaoBolsistaPesquisaBean.tipoBolsa.id == 0}">
				        	<td> <h:outputText value="#{de.tipoBolsa.descricaoResumida}"/> </td>
			        	</c:if>
	                   <td style="text-align: center;"><h:outputText value="#{de.dataIndicacao}"/></td>
	                   <td>
	                   		<c:set var="idPlanoTrabalho" value="#{de.planoTrabalho.id}"/>
							<html:link action="/pesquisa/indicarBolsista?dispatch=resumo&obj.id=${idPlanoTrabalho}">
								<img src="${ctx}/img/view.gif"
									alt="Visualizar Resumo da Indicação/Substituição"
									title="Visualizar Resumo da Indicação/Substituição"/>
							</html:link>
	                   </td>
	                   <td>
	                   		<h:commandLink id="ignorar" title="Ignorar Indicação de Bolsista" action="#{homologacaoBolsistaPesquisaBean.ignorarBolsista}" onclick="#{confirmDelete}">
	                   			<f:param name="id" value="#{de.id}" />
	                   			<h:graphicImage url="/img/pesquisa/remover_bolsista.gif" />
	                   		</h:commandLink>									
	                   </td>
	                 </tr>
	          	</c:forEach>
	        </tbody>
	
			<tfoot>
					<tr>
						<td colspan="${homologacaoBolsistaPesquisaBean.tipoBolsa.id == 0 ? 9 : 8}">
							<center>
								<h:commandButton value="Homologar Cadastro de Bolsistas Selecionados" action="#{ homologacaoBolsistaPesquisaBean.homologarBolsas }"/>
								<h:commandButton value="Cancelar" action="#{ homologacaoBolsistaPesquisaBean.cancelar }" immediate="true" onclick="#{confirm}"/>
							</center>
				    	</td>
				    </tr>
				</tfoot>																			
			</table>
		</c:if>

</h:form>
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
function checkAll(elem) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = elem.checked;
	});
}
</script>