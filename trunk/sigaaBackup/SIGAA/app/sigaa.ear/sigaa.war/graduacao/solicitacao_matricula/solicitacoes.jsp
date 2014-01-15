<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<style>
	table.listagem tr.titulo {
		border: 1px solid #CCC;
		border-width: 1px 0;
	}
</style>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h:outputText value="#{analiseSolicitacaoMatricula.create }" />
	<h2> <ufrn:subSistema /> > Análise de Solicitações de Matrícula</h2>

	<%@include file="/graduacao/solicitacao_matricula/info_solicitacoes.jsp" %>

	<c:set var="discente" value="#{analiseSolicitacaoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<c:set var="solicitacoes" value="#{analiseSolicitacaoMatricula.solicitacoes}"/>
	
	<c:if test="${not empty solicitacoes}">
	
	<center>
		<h:form>
			<h:graphicImage url="/img/report.png"></h:graphicImage>
			<h:commandLink value="Clique aqui " id="btSelecionardiscenteAnalisar" action="#{historico.selecionaDiscenteForm}">
				<f:param value="#{analiseSolicitacaoMatricula.discente.id}" name="id"/>
			</h:commandLink> 
			para visualizar o Histórico do discente
			
			<c:if test="${!analiseSolicitacaoMatricula.discente.discente.especial}">
				<br />
				<h:graphicImage url="/img/graduacao/matriculas/turmas_curriculo.png"></h:graphicImage>&nbsp;
				<c:if test="${!analiseSolicitacaoMatricula.discente.tecnico}">
					<h:commandLink id="btgerarRelatorioCurriculo" action="#{curriculo.gerarRelatorioCurriculo}" value="Clique Aqui" target="_blank">
						<f:param value="#{analiseSolicitacaoMatricula.discente.curriculo.id}" name="id"/>
					</h:commandLink>
				</c:if>
				<c:if test="${analiseSolicitacaoMatricula.discente.tecnico}">
					<h:commandLink id="btgerarRelatorioCurriculoTecnico" action="#{estruturaCurricularTecnicoMBean.view}" value="Clique Aqui" target="_blank">
						<f:param value="#{analiseSolicitacaoMatricula.discente.estruturaCurricularTecnica.id}" name="id"/>
					</h:commandLink>
				</c:if>
				para ver as disciplinas da estrutura curricular do discente.
			</c:if>
			<br />
			<h:graphicImage url="/img/graduacao/matriculas/escolha_restricoes.png"></h:graphicImage>
			<h:commandLink value="Clique aqui " id="btGerarRelatorioAnalises" action="#{analiseSolicitacaoMatricula.gerarRelatorioAnalises}" target="_blank" />
			para visualizar as todas as orientações já realizadas
		</h:form>
	</center>
	
	<br />
	<h:form>
		<table class="listagem" width="100%">
			<caption>Matrículas do Discente</caption>
			<thead>
			<tr>
				<td>Componente Curricular</td>
				<td width="8%">Horário</td>
				<td width="3%" style="text-align: right;">Turma</td>
				<td width="12%" style="text-align: center;">Solicitado em</td>
				<td colspan="2" width="30%" style="text-align:center">Orientação</td>
			</tr>
			</thead>
			<tbody>
				<tr class="titulo">
					<td colspan="6"> Orientar matrículas do discente </td>
				</tr>				
				<c:forEach items="${solicitacoes}" var="s" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"  style="font-size: 0.9em;">
					<td>
						<small>
						<a href="javascript:noop();" onclick="PainelComponente.show(${s.componente.id})" title="Ver Detalhes do Componente Curricular">
							${s.componente.descricao}
						</a>
						<c:if test="${s.negadaOutroPrograma and not analiseSolicitacaoMatricula.analiseOutroPrograma}">
							<br/> &nbsp;&nbsp;&nbsp;&nbsp; <b>(Negada pelo outro programa)</b>
						</c:if>
					 	</small>
					</td>
					<td>${s.turma.descricaoHorario}</td>
					<td style="text-align: right;">
					<a href="javascript:noop();" onclick="PainelTurma.show(${s.turma.id})" title="Ver Detalhes dessa turma">
					${s.turma.codigo}
					</a>
					</td>
					<td style="text-align: center;"><ufrn:format type="data" valor="${s.dataCadastro}" /></td>

					<c:choose>
						<c:when test="${s.discente.stricto and not empty s.matriculaGerada and not s.rematricula and analiseSolicitacaoMatricula.calendario.periodoReMatricula }">
							<td colspan="2" style="text-align: center">
								<b>${s.matriculaGerada.situacaoMatricula}</b> (<ufrn:format type="dataHora" valor="${s.matriculaGerada.dataCadastro}"/>)
								<c:if test="${s.vista or s.atendida}">
									<input type="hidden" name="aceitos" value="${s.id}" id="a_${s.id}" />
								</c:if>
								<c:if test="${s.orientada or s.negada}">
									<input type="hidden" name="negados" value="${s.id}" id="a_${s.id}" />
								</c:if>
							</td>
						</c:when>
						<c:otherwise>
							<td>
								<input type="checkbox" name="aceitos" value="${s.id}" id="a_${s.id}" 
								onclick="desmarcarOutro(this)" class="noborder" 
								${s.vista or s.atendida or (s.aguardandoOutroPrograma and not analiseSolicitacaoMatricula.analiseOutroPrograma) ? 'checked=checked' : '' } 
								${(s.negadaOutroPrograma and not analiseSolicitacaoMatricula.analiseOutroPrograma) ? 'disabled=disabled' : '' }>
								<label for="a_${s.id}"> ${analiseSolicitacaoMatricula.visto ? 'Visto' : 'Matricular' } </label>
							</td>
							<td>
								<input type="checkbox" name="negados" value="${s.id}" id="n_${s.id}" 
								onclick="desmarcarOutro(this)" class="noborder"  
								${s.orientada or s.negada or (s.negadaOutroPrograma and analiseSolicitacaoMatricula.analiseOutroPrograma) ? 'checked=checked' : '' } 
								${(s.negadaOutroPrograma and not analiseSolicitacaoMatricula.analiseOutroPrograma) ? 'disabled=disabled' : '' }>
								<label for="n_${s.id}">${analiseSolicitacaoMatricula.visto ? 'Orientar a não matricular' : 'Cancelar' }</label>
							</td>
						</c:otherwise>
					</c:choose>
				</tr>
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td colspan="6">
						<table align="right" id="observacoes_${s.id}" style="${s.vista || !s.orientada ? 'display:none' : ''}" width="100%">
						<tr>
						<td align="right" valign="top" style="font-size: xx-small" >
						Observações:<span class="required">&nbsp;</span>
						</td>
						<td align="left" width="95%">
						<textarea rows="2" name="obs_${s.id}" style="width: 90%">${s.observacao}</textarea>
						</td>
						</tr>
						</table>
					</td>
				</tr>
				</c:forEach>

				<tr>
					<td colspan="6"><br/></td>
				</tr>				
				
				<tr class="titulo">
					<td colspan="6"> Orientação geral de matrícula </td>
				</tr>
				<tr>
					<td colspan="6" style="text-align: justify;">
						<p style="padding: 5px;">
							Utilize o espaço abaixo para definir uma orientação para o discente quando esta não for específica 
							a um componente selecionado, ou para sugerir a matrícula em outros componentes.
						</p>
						<h:inputTextarea id="txOrientacao" value="#{analiseSolicitacaoMatricula.orientacao.orientacao}" rows="4" style="width: 95%;"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6" align="center">
						<h:commandButton id="btConfirmar" value="#{analiseSolicitacaoMatricula.confirmButton}" action="#{analiseSolicitacaoMatricula.confirmar}"/>&nbsp;
						<h:commandButton id="btVoltar" value="<< Voltar" action="#{analiseSolicitacaoMatricula.telaDiscentes}"/>&nbsp;
						<h:commandButton id="btCancelar" value="Cancelar" onclick="#{confirm}" action="#{analiseSolicitacaoMatricula.cancelar}" />					
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="required-items" align="center">
			<span class="required">&nbsp;</span>
			Itens de Preenchimento Obrigatório.
		</div>

	</h:form>

	</c:if>

	<c:if test="${empty solicitacoes}">
		Não há solicitações de matrícula pendentes.
	</c:if>

</f:view>

<script>

	/*
	function mostraObs(nomeTr, checked) {
		if ( !checked ) {
			document.getElementById(nomeTr).style.display='block';
		} else {
			document.getElementById(nomeTr).style.display='none';
		}
	}
	*/

	var prefixAceito = "a_";
	var prefixNegado = "n_";
	var prefixTxtArea = "observacoes_";

	function desmarcarOutro(check){

		var id = check.id.substring(2);
		var observacoes = getEl( prefixTxtArea + id ).dom;

		if( check.name == "aceitos"   ){
			var elemento = getEl( prefixNegado + id ).dom;
			elemento.checked = false;
			//var txtArea = getEl( prefixTxtArea + id );
			if( check.checked )
				observacoes.style.display='none';

		} else if( check.name == "negados"  ){
			var elemento = getEl( prefixAceito + id ).dom;
			elemento.checked = false;
			if( check.checked )
				observacoes.style.display='block';
			else
				observacoes.style.display='none';
		}
	}
		
	var checks = getEl(document).getChildrenByTagName('input');
	for (i = 0; i < checks.size(); i++) {
		if( checks[i].dom.type == 'checkbox' && checks[i].dom.checked ){
			desmarcarOutro(checks[i].dom);
		}
	}
</script>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>