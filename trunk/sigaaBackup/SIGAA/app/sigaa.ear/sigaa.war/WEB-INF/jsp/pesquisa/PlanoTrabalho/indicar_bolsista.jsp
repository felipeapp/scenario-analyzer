<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>
	<ufrn:subSistema /> &gt; Indicar/Substituir Bolsista
</h2>

<c:if test="${ not empty formPlanoTrabalho.referenceData.diaLimite }">
	<div class="descricaoOperacao">
		<p>
			Caro Coordenador,
		</p>
		<c:forEach items="${formPlanoTrabalho.referenceData.diaLimite}" var="entry">
			<p style="margin: 10px 20px; text-indent: 40px;"> O bolsista cuja bolsa seja <b>${entry.key}</b> deve ser indicado até o <b>${entry.value}º</b> dia do mês, 
				para que a bolsa seja efetivada no mês corrente. </p>
		</c:forEach>
	</div>
</c:if>

<html:form action="/pesquisa/indicarBolsista" method="post" focus="obj.membroProjetoDiscente.discente.pessoa.nome" styleId="form">

<table class="formulario" width="90%">
<caption> Indicar Bolsista </caption>
<tbody>
	<tr>
		<th width="25%" style="font-weight: bold"> Projeto de Pesquisa: </th>
		<td colspan="3"> ${formPlanoTrabalho.obj.projetoPesquisa.codigo} - ${formPlanoTrabalho.obj.projetoPesquisa.titulo} </td>
	</tr>
	<tr>
		<th style="font-weight: bold"> Orientador: </th>
		<td colspan="3">
			${formPlanoTrabalho.obj.orientador.pessoa.nome}
		</td>
	</tr>
	<tr>
		<th style="font-weight: bold"> Plano de Trabalho: </th>
		<td colspan="3">
			<ufrn:link action="/pesquisa/planoTrabalho/wizard" param="obj.id=${formPlanoTrabalho.obj.id}&dispatch=view">
			<c:choose>
				<c:when test="${ not empty  formPlanoTrabalho.obj.titulo}">
					${formPlanoTrabalho.obj.titulo}
				</c:when>
				<c:otherwise>
					<em> Título não definido </em>
				</c:otherwise>
			</c:choose>
			</ufrn:link>
		</td>
	</tr>
	<tr>
		<th style="font-weight: bold"> Tipo de Bolsa: </th>
		<td colspan="3">
			${formPlanoTrabalho.obj.tipoBolsaString}
		</td>
	</tr>

	<c:set var="tiposBolsa" value="${ formPlanoTrabalho.referenceData.tiposBolsa }" />
	<c:if test="${ not empty tiposBolsa and formPlanoTrabalho.obj.pendenteIndicacao }">
		<tr>
			<td colspan="4" class="subFormulario" style="text-align: center"> Definição do Tipo de Bolsa </td>
		</tr>

		<tr>
			<th class="required"> Selecione o tipo da bolsa: </th>
			<td colspan="3">
				<html:select property="tipoBolsa" style="width: 50%" onchange="document.getElementById('dispatch').value = 'tratarTipoBolsa'; submit(); javascript:mostrarDadosBancarios();">
					<html:options collection="tiposBolsa" property="key" labelProperty="value" />
				</html:select>
			</td>
		</tr>
		
	</c:if>

	<c:if test="${ not empty formPlanoTrabalho.bolsistaAtual }">
		<tr>
		<td colspan="4" class="subFormulario" style="text-align: center"> Finalização </td>
		</tr>
	</c:if>

	<c:if test="${not empty formPlanoTrabalho.bolsistaAnterior}">
	<tr>
		<th> <b>Bolsista Anterior:</b> </th>
		<td colspan="3">
		<c:choose>
			<c:when test="${not empty formPlanoTrabalho.bolsistaAnterior.discente}">
				${formPlanoTrabalho.bolsistaAnterior.discente}
			</c:when>
			<c:otherwise>
				<i> Não definido </i>
			</c:otherwise>
		</c:choose>
		</td>
	</tr>

	<tr>
		<th> <b>Motivo da Substituição:</b></th>
		<td colspan="3"> ${ formPlanoTrabalho.bolsistaAnterior.motivoSubstituicao } </td>
	</tr>
	</c:if>

	<c:if test="${ not empty formPlanoTrabalho.bolsistaAtual }">
		<tr>
			<th> <b>Bolsista Atual:</b> </th>
			<td colspan="3">
			<c:choose>
				<c:when test="${not empty formPlanoTrabalho.bolsistaAtual.discente}">
					${formPlanoTrabalho.bolsistaAtual.discente}
				</c:when>
				<c:otherwise>
					<i> Não definido </i>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<th class="required"> Data da Finalização: </th>
			<td colspan="3">
				<ufrn:calendar property="dataFinalizacao" />
			</td>
		</tr>
		<tr>
			<th class="required"> Motivo da Substituição:</th>
			<td>
				<html:select property="motivo" onchange="javascript: $('motivo').value = this.value;">
					<html:option value=""> -- SELECIONE O MOTIVO -- </html:option>
					<html:option value="SAÚDE"> SAÚDE </html:option>
					<html:option value="VÍNCULO EMPREGATÍCIO"> VÍNCULO EMPREGATÍCIO </html:option>
					<html:option value="MUDANÇA DE PROJETO"> MUDANÇA DE PROJETO </html:option>
					<html:option value="CONCLUSÃO DA GRADUAÇÃO"> CONCLUSÃO DA GRADUAÇÃO </html:option>
					<html:option value="A PEDIDO DO ALUNO"> A PEDIDO DO ALUNO </html:option>
					<html:option value="FALECIMENTO"> FALECIMENTO </html:option>
					<html:option value="OUTROS"> OUTROS </html:option>
				</html:select>
			</td>
			<td colspan="2">
				<html:text property="bolsistaAtual.motivoSubstituicao" size="40" styleId="motivo"/>
			</td>
		</tr>
	</c:if>

	<tr>
		<td colspan="4" class="subFormulario" style="text-align: center"> Indicação </td>
	</tr>
	<tr>
		<td colspan="4">
			<div class="descricaoOperacao">
				<center>
					<strong>Somente alunos que registraram interesse poderão ser indicados.</strong>
					<br />
					( Portal do Discente &gt; Menu Bolsas &gt; Oportunidades de Bolsa )
				</center>
			</div>
		</td>
	</tr>
	<tr>
		<th class="required"> Novo Bolsista: </th>
		<td colspan="3">
			<c:set var="idAjax" value="obj.membroProjetoDiscente.discente.id"/>
			<c:set var="nomeAjax" value="obj.membroProjetoDiscente.discente.pessoa.nome"/>
			<c:set var="nivel" value="${formPlanoTrabalho.niveisPermitidos}"/>
			<c:set var="ignorarUnidade" value="sim"/>
			<%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
		</td>
	</tr>
	<tr>
		<th class="required"> Data da Indicação: </th>
		<td colspan="3">
			<c:choose>
				<c:when test="${formPlanoTrabalho.permissaoGestor}">
					<ufrn:calendar property="dataIndicacao" />
				</c:when>
				<c:otherwise>
					<ufrn:format type=""></ufrn:format>
					${formPlanoTrabalho.dataIndicacao}				
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<table class="formulario" width="90%" id="dadosBancarios">
				<tr>
					<td colspan="6" style="color: red; text-align: center;">Para
						os bolsistas remunerados, informe abaixo seus dados bancários.
						Observação: a conta informada não pode ser Conta Conjunta ou Conta
						Poupança.
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Banco:</th>
					<td colspan="5"><html:select
						property="obj.membroProjetoDiscente.discente.pessoa.contaBancaria.banco.id"
						style="width: 70%">
						<html:option value="-1"> -- SELECIONE O BANCO DESEJADO -- </html:option>
						<html:options collection="bancos" property="id"
							labelProperty="codigoNome" />
					</html:select></td>
				</tr>
				<tr>
					<th class="obrigatorio">Agência:</th>
					<td width="15%"><html:text onblur="blockSpecialCharacter(this);"
						property="obj.membroProjetoDiscente.discente.pessoa.contaBancaria.agencia"
						size="15" />
					</td>
					<th width="5%" class="obrigatorio">Conta:</th>
					<td width="15%"><html:text onblur="blockSpecialCharacter(this);"
						property="obj.membroProjetoDiscente.discente.pessoa.contaBancaria.numero"
						size="15" />
					</td>
					<th width="5%" >Operação:</th>
					<td width="15%"><html:text onblur="blockSpecialCharacter(this);"
						property="obj.membroProjetoDiscente.discente.pessoa.contaBancaria.operacao"
						size="5" />
					</td>
				</tr>
				<tr>
					<th width="15%" class="obrigatorio">Tipo Conta:</th>
					<td colspan="5">
						<html:select property="obj.membroProjetoDiscente.tipoConta" style="width: 70%">
							<html:option value="-1"> -- SELECIONE O TIPO DE CONTA -- </html:option>
							<html:options collection="tipoConta" property="id" labelProperty="descricao" />
						</html:select>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<c:if test="${not empty formPlanoTrabalho.discentesAdesao}">
		<tr>
			<td class="subFormulario" colspan="4">
				<div class="infoAltRem">
					<html:img page="/img/report.png" style="overflow: visible;"/>: Histórico
					<html:img page="/img/comprovante.png" style="overflow: visible;"/>: Ver Qualificações do Aluno
				</div>
			</td>
		</tr>	
		<tr>
			<td class="subFormulario" colspan="4">Discentes que Realizaram Adesão ao Cadastro Único</td>
		</tr>
		<c:forEach items="${formPlanoTrabalho.discentesAdesao}" var="entry" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td colspan="3" width="95%">${entry.discente.matricula} - ${entry.discente.nome}  ${entry.prioritario ? '<br/><strong>[Prioritário (Segundo resolução Nº 169/2008-CONSEPE)]</strong>' : ''} </td>
				<td nowrap="nowrap" align="right">
					<a href="${ctx}/pesquisa/indicarBolsista.do?dispatch=verHistorico&idAluno=${entry.discente.id}">
						<html:img page="/img/report.png" style="overflow: visible;" title="Histórico"/>
					</a>
					<a href="${ctx}/pesquisa/indicarBolsista.do?dispatch=verQualificacao&idAluno=${entry.discente.id}&idPlanoTrabalho=${formPlanoTrabalho.obj.id}">
						<html:img page="/img/comprovante.png" style="overflow: visible;" title="Qualificações"/>
					</a>	
				</td>
			</tr>
		</c:forEach>
	</c:if>
</tbody>
<tfoot>
	<tr>
		<td colspan="4">
			<html:button dispatch="chamaModelo" value="Indicar"/>
			<html:button dispatch="popular" value="Cancelar" cancelar="true"/>
		</td>
	</tr>
</tfoot>
</table>

</html:form>

<br />
<center>
	<img src="${ctx}/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

<c:if test="${not empty formPlanoTrabalho.interessadoBolsa }">
		<div id="div-form">
			<div class="ydlg-hd">Qualificações do Aluno</div>
			<div class="ydlg-bd">
				<table class="formulario" width="100%" style="border: 0;">
					<caption> ${formPlanoTrabalho.interessadoBolsa.discente.nome} </caption>
					<tr>
						<th>Telefone:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.telefone}</td>
					</tr>
					<tr>
						<th>Email:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.email}</td>
					</tr>					
					<tr>
						<th>Lattes:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.linkLattes}</td>
					</tr>
					<tr>
						<th>Qualificações:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.qualificacoes}</td>
					</tr>							
				</table>
			</div>
		</div>
	</c:if>


<script type="text/javascript">
<!--
var PainelQualificacao = (function() {
	var painel;
	return {
        show : function(){
   	 		painel = new YAHOO.ext.BasicDialog("div-form", {
                modal: true,
                width: 400,
                height: 170,
                shadow: false,
                fixedcenter: true,
                resizable: false,
                closable: true
            });
       	 	painel.show();
        }
	};
})();

function blockSpecialCharacter(objResp) {
	var varString = new String(objResp.value);
		var i = new Number();
		var j = new Number();
		var cString = new String();
		var varRes = '';
	 
	for (i = 0; i < varString.length; i++) {
		cString = varString.substring(i, i + 1);
		
		if ( cString.match(['[@!#$%¨&*+_´`^~;:?áÁéÉíÍóÓúÚãÃçÇ|\?,./{}"<>()+=*¹²³£¢¬ªº° ]']) ){
			cString = '';
		}  
		
		varRes += cString;
	}
		objResp.value = varRes.toUpperCase();
}
</script>
<c:if test="${not empty formPlanoTrabalho.interessadoBolsa }">
	<script type="text/javascript">
	<!--
		PainelQualificacao.show();
	-->
	</script>
</c:if>

<script type="text/javascript">
function mostrarDadosBancarios(){
	
	var select = document.getElementsByName('tipoBolsa')[0];
	var tipo = select.options[select.selectedIndex].value;
	$('dadosBancarios').style.display = (tipo == 4 || tipo == 9) ? 'none' : 'table';
	
}

mostrarDadosBancarios();
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>