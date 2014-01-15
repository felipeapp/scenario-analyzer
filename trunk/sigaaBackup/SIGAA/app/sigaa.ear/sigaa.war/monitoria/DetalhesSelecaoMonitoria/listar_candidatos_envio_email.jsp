<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
function marcarTodos() {
	var re= new RegExp('selecionaDiscente', 'g')
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].checked = $('selectAll').checked ;
		}
	}
}
</script>
<f:view>
	<h2><ufrn:subSistema /> > Enviar E-mail para Candidatos Inscritos</h2>
	<h:form id="form">
		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p>Neste formulário você poderá enviar e-mails individuais ou para um grupo de discentes.</p>
			<p>Para enviar um e-mail individual, basta clicar no ícone correspondente. Para enviar para um grupo de discentes,
			 primeiro selecione os discentes da lista para depois clicar em "Enviar E-Mail para os Selecionados"</p> 
		</div>

		<div class="infoAltRem">
	     	<h:graphicImage value="/img/email_go.png" style="overflow: visible;" />: Enviar Email Individual
		</div>

		<table width="100%" class="formulario">
		    <caption>Lista de Candidatos Inscritos</caption>
		      <thead>
		      	<tr>
		      		<th width="5%" style="text-align: center;">
		      			<input type="checkbox" id="selectAll" onclick="marcarTodos();" title="Selecionar Todos"/> 
					</th>
		        	<th>Discente</th>
		        	<th></th>
		        </tr>
		      </thead>
		     <tbody>
    			<c:set var="lista" value="${ discenteMonitoria.provaSelecao.discentesInscritos }" />

	       		<c:if test="${empty lista}">
                    <tr> <td colspan="5" style="text-align: center;"> <font color="red">Não há discentes cadastrados neste projeto.</font> </td></tr>
			     </c:if>

			     <c:if test="${ not empty lista }">
			       	<c:forEach items="#{ discenteMonitoria.provaSelecao.discentesInscritos }" var="inscricao" varStatus="status">
				        <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
    	                    <td style="text-align: center;">
    	                    	<h:selectBooleanCheckbox value="#{ inscricao.discente.selecionado }" id="selecionaDiscente"/> 
   	                    	 </td>
    	                    <td> ${ inscricao.discente.matriculaNome } </td>
							<td width="2%">
								<h:commandLink title="Enviar Email Individual" action="#{ discenteMonitoria.preEnvioEmail }" 
									style="border: 0;">
								      <f:param name="id" value="#{inscricao.id}"/>
								      <h:graphicImage url="/img/email_go.png" />
								</h:commandLink>
							</td>
    	                    
	   	                </tr>
				    </c:forEach>
			     </c:if>
			     
     			<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton value="<< Voltar" action="#{provaSelecao.iniciarProcessoSeletivo}" id="cancelar"/>
							<h:commandButton value="Enviar E-Mail para os Selecionados" action="#{discenteMonitoria.preEnvioEmailGrupo}" id="enviarGrupo"/>
						</td>
					</tr>
				</tfoot>
			     	
		      </tbody>
	    </table>
	
	</h:form>    
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>