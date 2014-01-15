<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>



<style>
th.rotulo{ text-align: right; font-weight: bold; }
</style>
<f:view>
	<h2>Local de Prova</h2>
		<div class="descricaoOperacao">
		Prezado(a): <b>${acompanhamentoVestibular.obj.nome}</b>, verifique as informa��es apresentadas abaixo: 
	</div>
	<table class="formulario" width="90%">
		<caption>Local de Prova</caption>
		<tbody>
			<tr>
				<td colspan="6" class="subFormulario">Dados Pessoais</td>
			</tr>
			<tr>
				<th class="rotulo" >N�mero Inscri��o:</th>
				<td colspan="4">
					<h:outputText value="#{acompanhamentoVestibular.inscricaoVestibular.numeroInscricao}" />
				</td>
			</tr>
			<tr>
				<th class="rotulo" >CPF:</th>
				<td colspan="4">
					<ufrn:format type="cpf_cnpj" valor="${acompanhamentoVestibular.obj.cpf_cnpj}"></ufrn:format>
				</td>
				<td rowspan="8" valign="top" style="text-align: right;">
					<c:if test="${not empty acompanhamentoVestibular.arquivoUpload}">
						<rich:paint2D id="painter" width="150" height="200" paint="#{acompanhamentoVestibular.imagemFoto}" />
					</c:if>
					<c:if test="${empty acompanhamentoVestibular.arquivoUpload and not empty acompanhamentoVestibular.obj.idFoto}">
						<img src="${ctx}/verFoto?idArquivo=${acompanhamentoVestibular.obj.idFoto}&key=${ sf:generateArquivoKey(acompanhamentoVestibular.obj.idFoto) }" style="width: 150px; height: 200px"/>
					</c:if>
				</td>
			</tr>
			<c:if test="${not empty acompanhamentoVestibular.obj.passaporte}">
				<tr>
					<th class="rotulo">Passaporte:</th>
					<td colspan="5"><h:outputText
						value="#{acompanhamentoVestibular.obj.passaporte}" /></td>
				</tr>
			</c:if>
			<tr>
				<c:forEach items="#{acompanhamentoVestibular.inscricaoVestibular.opcoesCurso}" var="linha" varStatus="indice">
					<tr>
						<th class="rotulo">${indice.index + 1}� op��o de curso:</th>
						<td>${linha}</td>
					</tr>
				</c:forEach>
			</tr>
			<tr>
				<th class="rotulo" >L�ngua Estrangeira:</th>
				<td colspan="4">
					<h:outputText value="#{acompanhamentoVestibular.inscricaoVestibular.linguaEstrangeira.denominacao}" />
				</td>
			</tr>
			
			<tr>
				<th class="rotulo" >Local da Prova:</th>
				<td colspan="4">
					<h:outputText value="#{acompanhamentoVestibular.inscricaoVestibular.localProva.nome}" />
				</td>
			</tr>
			<tr>
				<th class="rotulo">Endere�o:</th>
				<td colspan="4">
					<h:outputText value="#{acompanhamentoVestibular.inscricaoVestibular.localProva.endereco}" />
				</td>
			</tr>
			<tr>
				<th class="rotulo">Bairro:</th>
				<td colspan="4">
					<h:outputText value="#{acompanhamentoVestibular.inscricaoVestibular.localProva.endereco.bairro}" />
				</td>
			</tr>
			<tr>
				<th class="rotulo">Cidade:</th>
				<td colspan="4">
					<h:outputText value="#{acompanhamentoVestibular.inscricaoVestibular.localProva.endereco.municipio.nome}" />
				</td>
			</tr>
			<tr>
				<th class="rotulo">Turma:</th>
				<td colspan="4">
					<h:outputText value="#{acompanhamentoVestibular.inscricaoVestibular.turma}" />
				</td>
			</tr>
		</tbody>
	</table>

	<div class="descricaoOperacao" style="width: 88%; margin-left: 47px;">
		<b>Avisos:</b> <br />
			<ul>
				<li>As provas ser�o aplicadas no per�odo de <ufrn:format type="data" valor="${acompanhamentoVestibular.processoSeletivo.inicioRealizacaoProva}" /> a <ufrn:format type="data" valor="${acompanhamentoVestibular.processoSeletivo.fimRealizacaoProva}" />.</li>
				<li>O acesso ao local onde se realizar�o as provas ocorrer� das 7h20min �s 8 horas (hor�rio oficial local).</li>
				<li>S�o de responsabilidade exclusiva do candidato a identifica��o correta do local de realiza��o das provas e o comparecimento no hor�rio determinado no Item 26 deste Edital.</li>
				<li>O candidato s� poder� realizar as provas no local divulgado pela COMPERVE.</li>
				<li>O candidato que chegar ap�s as 8 horas n�o ter� acesso ao local de realiza��o das provas naquele dia e estar� eliminado do Vestibular 2010.</li>
				<li>Para ter acesso � sala de provas, o candidato dever� apresentar o original do mesmo documento de identifica��o utilizado no ato de sua inscri��o.</li>			
			</ul>
	</div>
</f:view>
<center>
<a href="javascript: history.go(-1);"> << Voltar </a>
</center>
<%@include file="/public/include/rodape.jsp"%>