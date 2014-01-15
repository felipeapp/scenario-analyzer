<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<a4j:keepAlive beanName="boletimMedioMBean"/>
<style>
	.titulo {background: #E8E8E8; text-align: center; font-size: 20px; font-weight: bold; margin-bottom: 25px;}

	.listagem tr th {font-size: 10px !important; text-align: center;}
	.listagem tr .nota {font-size: 11px !important; text-align: center;}
	.listagem tr .disciplina {font-size: 11px !important; text-align: left;}
	
    .red {color: red;}	
	
	.bold {font-weight: bold;}
	.normal {font-weight: normal;}
	
	.text-right {text-align: right;}
	.text-left {text-align: left;}
	.text-center {text-align: center;}
	
</style>
<f:view>

<div class="titulo" style="background: #FFF;">Boletim Escolar - ${boletimMedioMBean.obj.matriculaSerie.turmaSerie.ano}</div>

<table class="listagem">
	<tr>
		<td colspan="4" class="titulo">Dados do Aluno</td>
	</tr>
	<tr>
		<td class="bold" style="width: 8%">Aluno(a):</td>
		<td class="bold">${boletimMedioMBean.obj.discente.nome}</td>
		<td class="bold text-right" colspan="2">Matrícula: ${boletimMedioMBean.obj.discente.matricula}</td>
	</tr>
	<tr>
		<td>Curso:</td>
		<td>${boletimMedioMBean.obj.discente.discente.curso.nome}</td>
		<td>Série/Turma: <span class="bold">${boletimMedioMBean.obj.matriculaSerie.turmaSerie.descricaoSerieTurma}</span></td>
		<td class="bold text-right">
			<c:if test="${boletimMedioMBean.obj.matriculaSerie.numeroChamada != null && boletimMedioMBean.obj.matriculaSerie.numeroChamada > 0}">
				N&deg; Chamada: ${boletimMedioMBean.obj.matriculaSerie.numeroChamada}
			</c:if>
		</td>
	</tr>
	<tr>
		<td>Situação:</td>
		<td colspan="3">${boletimMedioMBean.obj.matriculaSerie.descricaoSituacao}</td>
	</tr>
	<tr>
		<td colspan="4" class="titulo">Dados das Disciplinas</td>
	</tr>	
</table>

<c:set var="regrasNotas" value="#{boletimMedioMBean.obj.regraNotas}"/>
<table class="listagem" border="1">
	<tr>
		<th rowspan="2">DISCIPLINAS</th>	
		
		<c:set var="provaFinal" value=""/>
		<c:forEach items="#{regrasNotas}" var="item">
			<c:if test="${!item.provaFinal}">
				<th colspan="${item.nota ? '2' : '1'}" rowspan="${ item.recuperacao ? '2' : '1' }">
					<h:outputText value="#{item.titulo}"/>
				</th>
			</c:if>
			<c:if test="${item.provaFinal}">
				<c:set var="provaFinal" value="#{item.titulo}"/>		
			</c:if>
		</c:forEach>
				
		<th colspan="${not empty provaFinal ? '3' : '2'}">MÉDIA</th>
		<th rowspan="2">Faltas</th>
		<th rowspan="2">Situação</th>
	</tr>
	
	<tr>		
		<c:forEach items="#{regrasNotas}" var="item">
			<c:if test="${item.nota}">
				<th>Média</th>
				<c:if test="${item.nota}">
					<th>Faltas</th>						
				</c:if>
			</c:if>
		</c:forEach>			
		<th>Anual</th>	
		<c:if test="${not empty provaFinal}">
			<th>${provaFinal}</th>
		</c:if>
		<th>Final</th>		
	</tr>
		
	<c:forEach items="#{boletimMedioMBean.obj.notas}" var="itemDisc">
		<tr>
			<td class="disciplina">${itemDisc.matricula.componente.nome}</td>
			
			<c:set var="notaRecFinal" value="-"/>
			<c:forEach items="#{regrasNotas}" var="item">
				
				<c:if test="${item.nota || item.recuperacao}">
					
					<c:set var="nota" value="-"/>
					<c:set var="faltas" value="-"/>
					<c:set var="cor" value=""/>					
					<c:forEach items="#{itemDisc.notas}" var="itemNota">
						
						<c:if test="${itemNota.regraNota.id == item.id}">
							<c:set var="nota" value="#{itemNota.notaUnidade.nota}"/>
							<c:set var="faltas" value="#{itemNota.notaUnidade.faltas}"/>	
							<c:if test="${nota != '-' && nota < boletimMedioMBean.obj.mediaMinimaPassarPorMedia}">
								<c:set var="cor" value="red"/>		
							</c:if>						
						</c:if>
							
					</c:forEach>
					
					<td class="nota ${cor}">
						<c:choose>
							<c:when test="${nota ne null}">
								${nota}	
							</c:when>
							<c:otherwise>
								-
							</c:otherwise>
						</c:choose>
					</td>	
					<c:if test="${item.nota}">
						<td class="nota">
							<c:choose>
								<c:when test="${nota ne null and faltas ne null}">
									${faltas}	
								</c:when>
								<c:otherwise>
									-
								</c:otherwise>
							</c:choose>
						</td>
					</c:if>					
				</c:if>
				
			</c:forEach>			
			
			<c:set var="corMedia" value=""/>
			<c:if test="${itemDisc.matricula.mediaFinal != null && itemDisc.matricula.mediaFinal < boletimMedioMBean.obj.mediaMinimaPassarPorMedia}">
				<c:set var="corMedia" value="red"/>		
			</c:if>
			<c:set var="corPF" value=""/>
			<c:if test="${itemDisc.matricula.recuperacao != null && itemDisc.matricula.recuperacao < boletimMedioMBean.obj.mediaMinimaAprovacao}">
				<c:set var="corPF" value="red"/>		
			</c:if>						
			
			<c:if test="${itemDisc.matricula.consolidada}">
				<td class="nota ${corMedia}" style="width: 5%">
					${itemDisc.matricula.mediaParcial}
				</td>	
			</c:if>
			<c:if test="${!itemDisc.matricula.consolidada}">
				<td class="nota">-</td>
			</c:if>	
			<c:if test="${!empty provaFinal}">
				<c:if test="${itemDisc.matricula.consolidada}">
					<td class="nota ${corPF}" style="width: 5%">
						${itemDisc.matricula.recuperacao}
					</td>
				</c:if>
				<c:if test="${!itemDisc.matricula.consolidada}">
					<td class="nota">-</td>
				</c:if>					
			</c:if>
			<c:if test="${itemDisc.matricula.consolidada}">
				<td class="nota ${corMedia}" style="width: 5%">
					${itemDisc.matricula.mediaFinal}
				</td>
			</c:if>
			<c:if test="${!itemDisc.matricula.consolidada}">
				<td class="nota">-</td>
			</c:if>			
			<td class="nota" style="width: 5%">
				<c:if test="${itemDisc.matricula.consolidada}">
					${itemDisc.matricula.numeroFaltas}
				</c:if>
				<c:if test="${!itemDisc.matricula.consolidada}">
					-
				</c:if>			
			</td>
			<td class="nota" style="width: 5%">
				<c:if test="${itemDisc.matricula.consolidada}">
					${itemDisc.matricula.situacaoAbrev}				
				</c:if>
				<c:if test="${!itemDisc.matricula.consolidada}">
					-
				</c:if>
			</td>
			
		</tr>
	</c:forEach>
</table>

<c:if test="${!empty boletimMedioMBean.obj.observacoes}">
	<table class="listagem">
		<tr>
			<td class="titulo text-left">Observações:</td>
		</tr>
		<c:forEach items="#{boletimMedioMBean.obj.observacoes}" var="item">
			<tr>
				<td>${item.observacao}</td>
			</tr>
		</c:forEach>
	</table>
</c:if>


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>