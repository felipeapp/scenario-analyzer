<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> &gt; Finalizar de Bolsas no ${ configSistema['siglaSipac'] }</h2>

<h:form id="form">

	<div class="descricaoOperacao">
		<p><strong>Caro Gestor,</strong></p>
		<p>Esta operação lista os alunos indicados para modalidades de bolsas pagas pela própria instituição, 
		permitindo que sejam incluídas solicitações de bolsas no SIPAC automaticamente. Selecione uma modalidade de bolsa se desejar refinar a consulta.</p>
	</div>

	<table class="formulario" width="50%">
		<caption>Busca por Indicações de Bolsistas</caption>
		<tbody>

		    <tr>					
		    	<th width="30%" class="obrigatorio"> Modalidade da Bolsa:</th>
		    	<td>	    	
		    	 <h:selectOneMenu id="buscaTipoBolsa" value="#{ homologacaoBolsistaMBean.obj.bolsaAuxilio.tipoBolsaAuxilio.id }">
					<f:selectItem itemLabel="-- TODAS --" itemValue="0"/>
		    	 	<f:selectItems value="#{tipoBolsaAuxilioMBean.allCombo}" />
		    	 </h:selectOneMenu>
		    	 </td>
		    </tr>

		    <tr>					
		    	<th width="30%" class="obrigatorio"> Ano-Período:</th>
		    	<td>
		    		 <h:inputText value="#{ homologacaoBolsistaMBean.obj.ano }" size="3" maxlength="4"/> - <h:inputText value="#{ homologacaoBolsistaMBean.obj.periodo }" size="1" maxlength="1"/> 
		    	</td>
		    </tr>

		    <tr>					
		    	<th width="30%">Município:</th>
				<td>
					<h:selectOneMenu value="#{homologacaoBolsistaMBean.municipio.id}" disabled="#{homologacaoBolsistaMBean.readOnly}">
						<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
						<f:selectItems value="#{municipio.allAtivosCombo}" />
					</h:selectOneMenu>
		    	</td>
		    </tr>

		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
				<h:commandButton id="btBucar" value="Buscar" action="#{ homologacaoBolsistaMBean.buscar }"/>
				<h:commandButton id="btCancelar" value="Cancelar" action="#{ homologacaoBolsistaMBean.cancelar }" onclick="#{confirm}" immediate="true"/>
		    	</td>
		    </tr>
		</tfoot>
	</table>
	
	<br/>

	<c:if test="${not empty homologacaoBolsistaMBean.bolsas}">
		 <table class="listagem" width="100%">
		    <caption>Discentes Encontrados (${ fn:length(homologacaoBolsistaMBean.bolsas) })</caption>

		      <thead>
		      	<tr>
					<th><h:selectBooleanCheckbox value="false" onclick="checkAll(this)"/></th>		      	
		        	<th style="text-align: center;">Matrícula</th>
		        	<th>Nome</th>
		        	<th>Curso</th>
		        	<th>Tipo Auxílio</th>
		        	<th style="text-align: center;">Data da Solicitação</th>
		        	<th></th>
		        	<th></th>
		        </tr>
		 	</thead>
		 	<tbody>

	       		<c:forEach items="#{homologacaoBolsistaMBean.bolsas}" var="_bolsa" varStatus="status">				
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                   <td><h:selectBooleanCheckbox value="#{_bolsa.selecionado}" styleClass="check" rendered="#{ not empty _bolsa.tipoBolsaSIPAC }"/></td> 
	                   <td style="text-align: center;"><h:outputText value="#{_bolsa.discente.matricula}"/></td>
	                   <td><h:outputText value="#{_bolsa.discente.nome}"/></td>
	                   <td><h:outputText value="#{_bolsa.discente.curso.descricao}"/></td>
   		        	   <td><h:outputText value="#{_bolsa.tipoBolsaAuxilio.denominacao}"/> </td>
   		        	   <td style="text-align: center;"><h:outputText value="#{_bolsa.dataSolicitacao}"/> </td>
                   </tr>
	          	</c:forEach>
	        </tbody>
	
			<tfoot>
					<tr>
						<td colspan="${homologacaoBolsistaMBean.obj.bolsaAuxilio.tipoBolsaAuxilio.id == 0 ? 9 : 8}">
							<center>
								<h:commandButton value="Finalizar Bolsas Selecionados" action="#{ homologacaoBolsistaMBean.finalizarBolsas }"/>
								<h:commandButton value="Cancelar" action="#{ homologacaoBolsistaMBean.cancelar }" immediate="true" onclick="#{confirm}"/>
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