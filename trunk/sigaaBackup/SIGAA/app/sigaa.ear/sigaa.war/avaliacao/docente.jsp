
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>

<script type="text/javascript">
function limitText(limitField, limitCount, limitNum) {
	if (limitField.value.length > limitNum) {
		limitField.value = limitField.value.substring(0, limitNum);
	} else {
		$(limitCount).value = limitNum - limitField.value.length;
	}
}
</script>

<f:view>
<h:form id="form">

${ avaliacaoInstitucional.verificaAcessoDiscente }

<h2><ufrn:subSistema/> &gt; Questionário da Avaliação Institucional ${ avaliacaoInstitucional.obj.ano }.${ avaliacaoInstitucional.obj.periodo }</h2>

<c:set var="turmas" value="${ avaliacaoInstitucional.turmasDocente }"/>
<c:set var="aval" value="${ avaliacaoInstitucional.obj }"/>
<jsp:useBean id="perguntaAnterior" class="br.ufrn.sigaa.avaliacao.dominio.Pergunta"/>

<div class="descricaoOperacao">
<c:if test="${not empty avaliacaoInstitucional.formulario.instrucoesGerais }">
	${avaliacaoInstitucional.formulario.instrucoesGerais}
</c:if>
<c:if test="${empty avaliacaoInstitucional.formulario.instrucoesGerais }">
	<p>Esta avaliação é parte de um processo mais amplo de avaliação do Ensino Superior, determinado pela Lei
	Federal n&ordm; 10.861/04 e pela ${ avaliacaoInstitucional.resolucao }, executada pela ${ configSistema['siglaInstituicao'] } e tem em vista a melhoria das
	condições de ensino e de aprendizagem na graduação. O resultado será discutido pela comunidade acadêmica da
	${configSistema['siglaInstituicao']}. Suas respostas são de fundamental importância para a avaliação. A ${ configSistema['siglaInstituicao'] } agradece a sua participação.</p>
	<br/>
	<strong>Instruções para preenchimento:</strong><br/>
	1. Para as perguntas que requerem atribuição de notas, utilize a escala a seguir como referência:<br/>
	<strong>0, 1, 2, 3</strong> - Deficiente; <strong>4, 5, 6</strong> - Regular; <strong>7, 8</strong> - Bom; <strong>9, 10</strong> - Excelente; <strong>N/A</strong> - Sem condições de avaliar ou não se aplica.
	2. Para ver detalhes da turma que você está avaliando, basta clicar no código ou nome da disciplina.<br/><br/> 
	3. A qualquer momento você pode gravar as informações digitadas na avaliação clicando no botão <strong>Salvar</strong>. Se você desejar, poderá
	salvar os dados e continuar a avaliação em um outro momento.<br/><br/>
	4. A sua avaliação só será realmente enviada no momento em que você clicar no botão <strong>Finalizar</strong>.
</c:if>
</div>

<a4j:poll action="#{ avaliacaoInstitucional.salvar }" interval="300000"/>
<a4j:status startText="Salvando..."/>

<c:forEach var="grupo" items="#{ avaliacaoInstitucional.gruposDocente }">
<div id="dimensoes-avaliacao-${ grupo.id }" class="reduzido">
	<div id="elgen-8" class="ytab-wrap">
		<div class="ytab-strip-wrap">
		<table id="elgen-10" class="ytab-strip" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr id="elgen-9" class="">
					<td id="elgen-15" class="on" style="width: 165px;" align="center">
						<a id="elgen-12" class="ytab-right" href="#"> 
						<span class="ytab-left"> 
						<em id="elgen-13" class="ytab-inner">
						<span id="elgen-14" class="ytab-text" title="${ grupo.titulo }" unselectable="on">${ grupo.titulo }</span> 
						</em></span>
						</a>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
	</div>
	
<div id="dim${ grupo.id }" class="aba">

<h3>${ grupo.descricao }</h3>

	<c:if test="${ grupo.avaliaTurmas }">
	<table width="100%" class="turma"><td width="420">&nbsp;</td>
	<c:forEach var="t" items="${ turmas }">
	<td width="50" align="center"><a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="Ver Detalhes dessa turma">${ t.disciplina.codigo }</a></td>
	</c:forEach><td></td>
	</table>
	</c:if>

	<c:forEach items="#{ grupo.perguntas }" var="p" varStatus="loop">
	
	<c:if test="${ (not grupo.avaliaTurmas) && (p.avaliarTurmas) && (perguntaAnterior.id == 0 || not perguntaAnterior.avaliarTurmas) }">
	<table width="100%" class="turma"><td width="425">&nbsp;</td>
	<c:forEach var="t" items="${ turmas }">
	<td width="50" align="center"><a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="Ver Detalhes dessa turma">${ t.disciplina.codigo }</a></td>
	</c:forEach><td></td>
	</table>
	</c:if>
	
	<table width="100%" ${ sf:contains(avaliacaoInstitucional.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
	
	<c:if test="${ p.nota }">
		<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="420">${ loop.index + 1 }. ${ p.descricao }</td>
			<c:if test="${ p.avaliarTurmas }">
				<c:forEach var="t" items="${ turmas }" varStatus="irep">
					<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }"/></td>
				</c:forEach>
			</c:if>
			<c:if test="${ not p.avaliarTurmas }">
				<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"/></td>
			</c:if>
			<td></td>
		</tr>
	</c:if>	
	
	<c:if test="${ p.escolhaUnica }">
		<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
		<c:forEach var="alt" items="${ p.alternativas }">
			<tr><td width="20"></td>
			<td width="10">
				<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
				<td>${ alt.descricao }
				<c:if test="${ alt.permiteCitacao }">
					<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
				</c:if>
				</td>
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${ p.multiplaEscolha }">
		<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
		<c:forEach var="alt" items="${ p.alternativas }">
			<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
			<td>${ alt.descricao }
			<c:if test="${ alt.permiteCitacao }">
				<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
			</c:if>
			</td></tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${ p.simNao }">
		<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
		<c:if test="${ p.avaliarTurmas }">
			<c:forEach var="t" items="${ turmas }">
			<td width="50"><aval:simNao pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }"/></td>
			</c:forEach>
		</c:if>
		<c:if test="${ not p.avaliarTurmas }">
			<td width="50"><aval:simNao pergunta="${ p.id }" aval="${ aval }"/></td>
		</c:if><td></td>
		</tr>
	</c:if>
	
	</table>
	<c:set value="${ p }" var="perguntaAnterior"/>
	</c:forEach>
	
	<br/>
	</div>
	</div><br/>
</c:forEach>


<div id="dimensao-comentario" class="reduzido">
	<div id="elgen-8" class="ytab-wrap">
		<div class="ytab-strip-wrap">
		<table id="elgen-10" class="ytab-strip" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr id="elgen-9" class="">
					<td id="elgen-15" class="on" style="width: 165px;" align="center">
						<a id="elgen-12" class="ytab-right" href="#"> 
						<span class="ytab-left"> 
						<em id="elgen-13" class="ytab-inner">
						<span id="elgen-14" class="ytab-text" title="Comentários Adicionais" unselectable="on">Comentários Adicionais</span> 
						</em></span>
						</a>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
	</div>
	
<div id="comentario" class="aba">

<h3>ESPAÇO DESTINADO PARA COMENTÁRIOS OPCIONAIS</h3>

<center>
<c:if test="${ not empty avaliacaoInstitucional.turmasDocente }">
	<h5>Comentários Específicos sobre a Turma (marque a turma específica)</h5>
</c:if>

<table width="100%">
<c:forEach var="dt" items="#{ avaliacaoInstitucional.turmasDocente }">
<tr><td>
<c:set value="" var="na"/>
<c:if test="${dt.disciplina.excluirAvaliacaoInstitucional}"><c:set value="(NA)" var="na"/></c:if>
<aval:observacaoSimples aval="${ aval }" dtId="${ dt.id }" codigo="${ dt.turma.descricaoCodigo }  ${na}"  readOnly="${dt.disciplina.excluirAvaliacaoInstitucional}"/>
</td></tr>
</c:forEach>
</table>
<h5>Comentários Gerais</h5>
<h:inputTextarea value="#{ avaliacaoInstitucional.obj.observacoes }" rows="8" cols="105" id="comment" style="width: 99%" onkeydown="limitText(this, commentCount, 600);"/>
Você pode digitar <input readonly type="text" id="commentCount" size="3" value="${600 - fn:length(avaliacaoInstitucional.obj.observacoes) < 0 ? 0 : 600 - fn:length(avaliacaoInstitucional.obj.observacoes)}"> caracteres.
</center>

</div>
</div>


<table class="botoes">
	<tr>
		<td style="text-align: center;"><h:commandButton id="btnCancelar" value="Cancelar" image="/img/consolidacao/nav_left_red.png" action="#{ avaliacaoInstitucional.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a avaliação?')) { return false; }"/></td>
		<td style="text-align: center;"><h:commandButton id="btnSalvar" value="Salvar" image="/img/consolidacao/disk_green.png" action="#{ avaliacaoInstitucional.salvar }"/></td>
		<td style="text-align: center;"><h:commandButton id="btnFinalizar" value="Finalizar" image="/img/consolidacao/disk_blue_ok.png" action="#{ avaliacaoInstitucional.finalizar }" onclick="if (!confirm('Deseja realmente finalizar a avaliação?')) { return false; }"/></td>
	</tr>
	<tr>
		<td style="text-align: center;"><h:commandLink id="lnkCancelar" value="Cancelar" action="#{ avaliacaoInstitucional.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a avaliação?')) { return false; }"/></td>
		<td style="text-align: center;"><h:commandLink id="lnkSalvar" value="Salvar" action="#{ avaliacaoInstitucional.salvar }"/> </td>
		<td style="text-align: center;"><h:commandLink id="lnkFinalizar" value="Finalizar" action="#{ avaliacaoInstitucional.finalizar }" onclick="if (!confirm('Deseja realmente finalizar a avaliação?')) { return false; }"/></td>
	</tr>
</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript" src="${ctx}/javascript/avaliacao.js"></script>
