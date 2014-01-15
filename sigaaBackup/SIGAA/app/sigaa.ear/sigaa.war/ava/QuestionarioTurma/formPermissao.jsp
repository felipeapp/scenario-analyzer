<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />
	<a4j:keepAlive beanName="categoriaPerguntaQuestionarioTurma" />

	<style>
		.botao-medio {
				margin-bottom:0px !important;
				height:60px !important;
		}
	</style>

	<%@include file="/ava/menu.jsp" %>
	<h2>  <ufrn:subSistema /> &gt; Permiss�es da Categoria</h2>
	<br />
	<h:form>
		<div class="descricaoOperacao">
			<p>Prezado(a) docente,</p>
			
			<p> Por favor, confirme a cria��o da permiss�o com os dados abaixo. </p>
		</div>
	
		<table class="formulario" width="500px">
			<caption>Cadastro de Permiss�o</caption>
			
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:outputText value="#{categoriaPerguntaQuestionarioTurma.permissao.usuario.pessoa.nome}" /></td>
				<th class="obrigatorio">Categoria:</th>
				<td><h:outputText value="#{categoriaPerguntaQuestionarioTurma.obj.nome}" /></td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Confirmar" action="#{categoriaPerguntaQuestionarioTurma.cadastrar}" id="acao"/>
					<h:commandButton value="<< Voltar"  action="#{categoriaPerguntaQuestionarioTurma.voltarAoCadastroDePergunta}" rendered="#{ categoriaPerguntaQuestionarioTurma.voltarAoCadastroDePergunta }" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{categoriaPerguntaQuestionarioTurma.cancelar}" immediate="true" id="cancelar"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>