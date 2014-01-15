<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<f:view>
	<h2><ufrn:subSistema /> > Resumo do projeto base</h2>

<h:form>

		<table class="formulario" width="99%">
			<caption>Resumo do projeto</caption>

			<%@include file="/projetos/ProjetoBase/dados_projeto.jsp"%>

			<!-- BOTÕES -->
			<table class=formulario width="100%">
				<tfoot>
					<tr>
						<td colspan="2">
						  <h:commandButton
							value="Finalizar Edição e Enviar"
							action="#{ projetoBase.submeterProjeto }" id="btSubmeterProjeto"
							rendered="#{ !projetoBase.membroComiteAlterandoCadastro }" /> 
						  <h:commandButton
							value="Gravar (Rascunho)"
							action="#{ projetoBase.gravarFinalizar }" id="btGravarProjeto" />
						  <h:commandButton value="<< Voltar"
							action="#{ projetoBase.passoAnterior }"
							id="btPassoAnteriorResumo" /> 
						<h:commandButton value="Cancelar"
							action="#{ projetoBase.cancelar }" id="btCancelar"
							onclick="#{confirm}" /></td>
					</tr>
				</tfoot>
			</table>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>