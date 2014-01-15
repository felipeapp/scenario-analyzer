<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Participação no ENADE</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>O ENADE - Exame Nacional de Desempenho de Estudantes - é aplicado aos estudantes ingressantes e concluintes de cada curso a ser avaliado.</p>
		<p>O ENADE é componente curricular obrigatório dos cursos superiores, devendo constar do
histórico escolar de todo estudante a participação ou dispensa da prova, nos termos constantes na Portaria
Normativa vigente, publicada pelo MEC.</p>
		<p>Através deste formulário, você poderá definir os tipos de participação do Discente no ENADE, como, por exemplo, "Dispensado em função do curso não ser avaliado no ano" ou
		"Discente inscrito no ENADE", os quais constarão no Histórico do discente.</p>
		<br/>
		<p>Para melhores informações, verifique a legislação atual, bem como as portarias vigentes do MEC.</p>
	</div>
	<br/>
	<h:form id="form">
		<a4j:keepAlive beanName="participacaoEnade"></a4j:keepAlive>
		<table class="formulario" >
			<caption class="formulario">Dados da Participação no ENADE</caption>
			<tbody>
				<tr>
					<th class="required">Tipo do ENADE:</th>
					<td>
						<h:selectOneMenu value="#{participacaoEnade.obj.tipoEnade}" id="tipoEnade" disabled="#{participacaoEnade.obj.id > 0 }" >
							<f:selectItems value="#{participacaoEnade.tipoEnadeCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Descrição:</th>
					<td>
						<h:inputText id="descricao" value="#{participacaoEnade.obj.descricao}" size="120"  maxlength="120" 
							disabled="#{participacaoEnade.readOnly}" readonly="#{participacaoEnade.readOnly}" />
					</td>
				</tr>
				<tr>
					<th>
						 <h:selectBooleanCheckbox value="#{participacaoEnade.obj.participacaoPendente}" id="pendencia" disabled="#{participacaoEnade.readOnly}" >
						 </h:selectBooleanCheckbox>
					</th>
					<td width="30%">Indica que há pendência no histórico do discente</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{participacaoEnade.confirmButton}" action="#{participacaoEnade.cadastrar}" id="cadastrar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{participacaoEnade.cancelar}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>
	<br>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<br>

<script type="text/javascript">

	$('cadastroCurso:nome').focus();

	function escolherInstituicao(rede) {
		if (rede.checked) {
			$('instituicao').show();
		} else {
			$('instituicao').hide();
		}
		$('cadastroCurso:checkCursoRede').focus();
	}
	
	escolherInstituicao($('cadastroCurso:checkCursoRede'));
	
</script>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
