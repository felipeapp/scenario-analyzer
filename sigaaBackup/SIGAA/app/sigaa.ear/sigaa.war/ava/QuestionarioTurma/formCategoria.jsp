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
	<h2>  <ufrn:subSistema /> &gt; Categorias de questões</h2>
	<br />
	<h:form>
		<div class="descricaoOperacao">
			<p>Prezado(a) docente,</p>
			
			<p> Informe uma descrição para uma nova categoria de perguntas. Tal descrição remete geralmente a um tema específico ou a uma área de conhecimento. </p>
		</div>
	
		<h:inputHidden value="#{categoriaPerguntaQuestionarioTurma.obj.id}"/>
		<h:inputHidden value="#{categoriaPerguntaQuestionarioTurma.confirmButton}"/>

		<h:messages showDetail="true" />

		<table class="formulario" width="500px">
			<caption>Categoria de perguntas</caption>
			
			<tr>
				<th class="required">Nome:</th>
				<td>&nbsp;<h:inputText value="#{categoriaPerguntaQuestionarioTurma.obj.nome}" readonly="#{categoriaPerguntaQuestionarioTurma.readOnly}" maxlength="100" size="55" onkeyup="CAPS(this);"/></td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="#{categoriaPerguntaQuestionarioTurma.confirmButton}" action="#{categoriaPerguntaQuestionarioTurma.cadastrar}" id="acao"/>
					<h:commandButton value="<< Voltar"  action="#{categoriaPerguntaQuestionarioTurma.listar}" rendered="#{ !categoriaPerguntaQuestionarioTurma.voltarAoCadastroDePergunta }" />
					<h:commandButton value="<< Voltar"  action="#{categoriaPerguntaQuestionarioTurma.voltarAoCadastroDePergunta}" rendered="#{ categoriaPerguntaQuestionarioTurma.voltarAoCadastroDePergunta }" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{categoriaPerguntaQuestionarioTurma.cancelar}" immediate="true" id="cancelar"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<div align="center">
			<span class="required">&nbsp;</span>
			Campos de Preenchimento Obrigatório
		</div>
	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>