<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

<h2><ufrn:subSistema /> &gt; Finaliza��o de Bolsas de Inicia��o � Pesquisa</h2>

<h:form id="form">

	<div class="descricaoOperacao">
		<p><strong>Caro Gestor,</strong></p>
		<p>Esta opera��o lista os alunos finalizados das bolsas de modalidades pagas pela pr�pria institui��o, 
		permitindo que suas bolsas sejam finalizadas no SIPAC automaticamente. Selecione uma modalidade de bolsa se desejar refinar a consulta.</p>
		<p>A listagem apresenta apenas alunos que foram finalizados no SIGAA mas ainda possuem bolsa ativa no SIPAC.
		Caso identifique alguma inconsist�ncia, como alunos aparecendo repetidos, utilize a op��o para ignorar a finaliza��o do(s) caso(s) incorreto(s).</p> 
	</div>

	<table class="formulario" width="50%">
		<caption>Busca por Finaliza��es de Bolsistas</caption>
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
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Resumo da Finaliza��o/Substitui��o
		    <h:graphicImage url="/img/pesquisa/remover_bolsista.gif" style="overflow: visible;"/>: Ignorar Finaliza��o de Bolsista
		</div>
		 <table class="listagem" width="100%">
		    <caption>Discentes Encontrados (${ fn:length(discentesBolsistasSigaa) })</caption>

		      <thead>
		      	<tr>
					<th><h:selectBooleanCheckbox value="true" onclick="checkAll(this)"/></th>		      	
		        	<th>Matr�cula</th>
		        	<th>Nome</th>
		        	<th>Curso</th>
		        	<th>C�d. Projeto</th>
		        	<c:if test="${homologacaoBolsistaPesquisaBean.tipoBolsa.id == 0}">
			        	<th>Modalidade</th>
		        	</c:if>
		        	<th style="text-align: center;">Data da Finaliza��o</th>
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
	                   <td style="text-align: center;"><h:outputText value="#{de.dataFinalizacao}"/></td>
	                   <td>
	                   		<c:set var="idPlanoTrabalho" value="#{de.planoTrabalho.id}"/>
							<html:link action="/pesquisa/indicarBolsista?dispatch=resumo&obj.id=${idPlanoTrabalho}">
								<img src="${ctx}/img/view.gif"
									alt="Visualizar Resumo da Finaliza��o/Substitui��o"
									title="Visualizar Resumo da Finaliza��o/Substitui��o"/>
							</html:link>
	                   </td>
	                   <td>
	                   		<h:commandLink id="ignorar" title="Ignorar Finaliza��o de Bolsista" action="#{homologacaoBolsistaPesquisaBean.ignorarBolsista}" onclick="#{confirmDelete}">
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
								<h:commandButton value="Finalizar Bolsistas Selecionados" action="#{ homologacaoBolsistaPesquisaBean.finalizarBolsas }"/>
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