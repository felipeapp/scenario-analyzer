<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Participa��o no ENADE</h2>

	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>O ENADE - Exame Nacional de Desempenho de Estudantes - � aplicado aos estudantes ingressantes e concluintes de cada curso a ser avaliado.</p>
		<p>O ENADE � componente curricular obrigat�rio dos cursos superiores, devendo constar do
hist�rico escolar de todo estudante a participa��o ou dispensa da prova, nos termos constantes na Portaria
Normativa vigente, publicada pelo MEC.</p>
		<p>Atrav�s deste formul�rio, voc� poder� definir os tipos de participa��o do Discente no ENADE, como, por exemplo, "Dispensado em fun��o do curso n�o ser avaliado no ano" ou
		"Discente inscrito no ENADE", os quais constar�o no Hist�rico do discente.</p>
		<br/>
		<p>Para melhores informa��es, verifique a legisla��o atual, bem como as portarias vigentes do MEC.</p>
	</div>
	<br/>
	<h:form id="form">
		<a4j:keepAlive beanName="participacaoEnade"></a4j:keepAlive>
		<table class="formulario" >
			<caption class="formulario">Dados da Participa��o no ENADE</caption>
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
					<th class="required">Descri��o:</th>
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
					<td width="30%">Indica que h� pend�ncia no hist�rico do discente</td>
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
