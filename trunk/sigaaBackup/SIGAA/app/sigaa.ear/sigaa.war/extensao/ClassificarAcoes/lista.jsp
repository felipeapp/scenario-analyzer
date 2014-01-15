<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Pr�-Classificar A��es de Extens�o	</h2>


<div class="descricaoOperacao">
	<b>Prezado(a) Gestor(a),</b><br/> selecione um edital e clique em Pr�-classificar para que o SIGAA realize a classifica��o 
	das A��es de Extens�o, vinculadas ao edital selecionado, de acordo com as avalia��es realizadas pelo Comit� Ad hoc.<br/>
	A classifica��o apresentada poder� ser confirmada atrav�s do bot�o 'Confirmar Classifica��o' que ser� apresentado no final da lista. 
	Mas aten��o, uma vez confirmada a classifica��o ela n�o poder� ser realizada novamente e o processo de avalia��o do 
	comit� Ad hoc ser� finalizado (todas as avalia��es n�o finalizadas ser�o canceladas).<br/>
	A confirma��o da classifica��o grava as m�dias obtidas por cada proposta possibitando a avalia��o dos projetos pelos 
	membros do Comit� de Extens�o.
</div>


<a4j:keepAlive beanName="classificarAcaoExtensao" />
<h:form id="form">
	<table class="formulario" width="40%">
		<caption>Editais</caption>
		<tbody>
			<tr>
				<th><h:outputLabel for="edital">Edital:</h:outputLabel></th>
				<td>    	
					<h:selectOneMenu id="edital" value="#{ classificarAcaoExtensao.edital.id }">
						<f:selectItems value="#{classificarAcaoExtensao.editais}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
			 		<h:commandButton action="#{ classificarAcaoExtensao.iniciarClassificacao }" value="Pr�-classificar" id="cmdBtClassficar"/>
					<h:commandButton action="#{ classificarAcaoExtensao.cancelar }" value="Cancelar" onclick="#{confirm}" id="cmdBtCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>