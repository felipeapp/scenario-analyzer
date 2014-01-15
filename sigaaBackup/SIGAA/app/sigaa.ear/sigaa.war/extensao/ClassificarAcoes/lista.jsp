<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Pré-Classificar Ações de Extensão	</h2>


<div class="descricaoOperacao">
	<b>Prezado(a) Gestor(a),</b><br/> selecione um edital e clique em Pré-classificar para que o SIGAA realize a classificação 
	das Ações de Extensão, vinculadas ao edital selecionado, de acordo com as avaliações realizadas pelo Comitê Ad hoc.<br/>
	A classificação apresentada poderá ser confirmada através do botão 'Confirmar Classificação' que será apresentado no final da lista. 
	Mas atenção, uma vez confirmada a classificação ela não poderá ser realizada novamente e o processo de avaliação do 
	comitê Ad hoc será finalizado (todas as avaliações não finalizadas serão canceladas).<br/>
	A confirmação da classificação grava as médias obtidas por cada proposta possibitando a avaliação dos projetos pelos 
	membros do Comitê de Extensão.
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
			 		<h:commandButton action="#{ classificarAcaoExtensao.iniciarClassificacao }" value="Pré-classificar" id="cmdBtClassficar"/>
					<h:commandButton action="#{ classificarAcaoExtensao.cancelar }" value="Cancelar" onclick="#{confirm}" id="cmdBtCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>