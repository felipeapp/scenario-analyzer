<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<f:view>
	<h2><ufrn:subSistema /> > Resumo da A��o de Extens�o</h2>

<h:form>
<div class="descricaoOperacao">
	<table>
		<tr>
			<td><html:img page="/img/warning.gif"/> </td>
			<td style="text-align: justify;">
				<p>
					<b>Aten��o:</b> Ao submeter este formul�rio, o mesmo ser� enviado para aprova��o dos departamentos envolvidos.	
				</p>
			</td>
		</tr>	
	</table>
</div>
<table class="formulario" width="99%">
	<caption class="listagem"> RESUMO DA A��O </caption>

	<%@include file="/extensao/Atividade/include/dados_atividade.jsp"%>
	
	<!-- BOTOES -->
	<tfoot>
	<tr><td colspan="2">
		<h:commandButton value="Submeter � aprova��o" action="#{ atividadeExtensao.submeterAtividadeDepartamentosEnvolvidos }" id="submeterDep"/>		
		<h:commandButton value="Gravar (Rascunho)" action="#{ atividadeExtensao.gravarFinalizar }" id="gravar" />
		<h:commandButton value="<< Voltar" action="#{ atividadeExtensao.passoAnterior }" id="voltar"/>		
		<h:commandButton value="Cancelar" action="#{ atividadeExtensao.cancelar }" id="cancelar" onclick="#{confirm}"/>
	</td></tr>
	</tfoot>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>