<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style type="text/css">
div.opcoes { margin: 5px 0; }
div.opcoes a { font-size: 0.9em; }
tr.alunoSelecionado { background: #FF8888; }

h3 { text-align: center; font-variant: small-caps; border-bottom: 1px solid #BBB; margin-bottom: 5px;}
#relatorio th { font-weight: bold; }
#relatorio .linha { border-bottom: 1px dashed #BBB; }
#relatorio table tr td, #relatorio table tr th { font-size: 0.9em }
#relatorio .nota { width: 48px; text-align: center; }
#relatorio .situacao { width: 85px; text-align: center; }

#identificacao {border-bottom: 1px solid #CCC; width: 100%;}
#identificacao td { font-size: 1.1em; padding: 2px; }
</style>

<f:view>

<c:if test="${ relatorioNotasAluno.ead }">
<c:set var="recuperacao" value="Repos."/>
</c:if>
<c:if test="${ !relatorioNotasAluno.ead }">
<c:set var="recuperacao" value="Recuperação"/>
</c:if>

	<h:outputText value="#{ relatorioNotasAluno.create }"/>

	<h3> Relatório de Notas do Aluno(a)</h3>

	<table id="identificacao">
		<tr>
			<td width="168">
				<img src="/sigaa/img/celular.jpg"/>
			</td>
			<td>
				<table>
					<tr>
						<th> Aluno(a): </th>
						<td> ${ relatorioNotasAluno.discente.pessoa.nome } - ${ relatorioNotasAluno.discente.matricula } </td>
					</tr>
					<tr>
						<th> Curso: </th>
						<td> ${relatorioNotasAluno.discente.curso} </td>
					</tr>
				</table>
			</td>
			<td>
				<%-- 
				<big>
				<big>
				<c:if test="${relatorioNotasAluno.discente.nivelStr == 'G'}">
				<c:forEach items="${relatorioNotasAluno.discente.discente.indices}" var="indice" varStatus="status">
					<b>${indice.indice.sigla} : </b> ${indice.valor}
				</c:forEach>	
				</c:if>
				</big>
				</big>
				--%>			
			</td>
			
	</table>

		<div class="notas" style="clear: both;">
		<c:set var="ano" value="0"/>
		<c:set var="periodo" value="0"/>
		<c:forEach var="matricula" items="${ relatorioNotasAluno.matriculas }" varStatus="i">

			<c:if test="${ matricula.ano != ano || matricula.periodo != periodo }">
			<c:set var="ano" value="${ matricula.ano }"/>
			<c:set var="periodo" value="${ matricula.periodo }"/>

			<c:if test="${ ano != 0 || periodo != 0 }">
			</tbody>
			</table>
			<br/>
			</c:if>

			<table class="tabelaRelatorio" width="100%">
			<caption>${ ano }.${ periodo }</caption>
				<thead>
					<tr>
						<th width="7%" >Código</th>
						<th>Disciplina</th>
							
							<c:if test="${ !relatorioNotasAluno.discente.stricto }">
								<c:forEach var="unid" begin="1" end="${ matricula.qtdNotas }">
									<c:if test="${ relatorioNotasAluno.ead && not empty matricula.metodologiaAvaliacao && matricula.metodologiaAvaliacao.permiteTutor }">
										<th class="nota">Prof. <c:if test="${ matricula.metodologiaAvaliacao.duasProvas }">${ unid }</c:if></th>
										<th class="nota">Tutor <c:if test="${ matricula.metodologiaAvaliacao.duasProvas }">${ unid }</c:if></th>
									</c:if>
									<th class="nota" nowrap="nowrap" >
										Unidade. ${ unid }
									</th>
								</c:forEach>
								<c:if test="${ !relatorioNotasAluno.discente.lato }">
								<th class="nota" nowrap="nowrap" >${ recuperacao }</th>
								</c:if>
							</c:if>
							<th class="nota" nowrap="nowrap" >Resultado</th>
						
							<c:if test="${ !relatorioNotasAluno.ead }">
									<th class="nota">Faltas</th>
							</c:if>
						
							<th class="situacao">Situação</th>
					</tr>
				</thead>
				<tbody>
			</c:if>

			<tr class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' } linha">
				<td nowrap="nowrap">${ matricula.componente.codigo }</td>
			<td nowrap="nowrap" style="border-right: 1px solid #888;">${ matricula.componente.nome }</td>
				<c:if test="${ !relatorioNotasAluno.discente.stricto }">
				<c:forEach var="undNota" begin="0" end="${ matricula.qtdNotas-1 }">
				
					<td class="nota" style="border-right: 1px solid #888;">
				  	
						<c:if test="${ ((not empty matricula.notas) || (matricula.notas[undNota] != null) || (matricula.notas[undNota].id != 0)) 
									&& ((usuario.discenteAtivo.id == matricula.discente.id && !matricula.ocultarNotas)
									|| (usuario.discenteAtivo.id != matricula.discente.id))}">
							<fmt:formatNumber pattern="#0.0" value="${ matricula.notas[undNota].notaPreenchida }" />
						</c:if>
					</td>
					
						<c:if test="${ relatorioNotasAluno.ead && not empty matricula.metodologiaAvaliacao && matricula.metodologiaAvaliacao.permiteTutor }">					
							<td class="nota" style="border-right: 1px solid #888;">
							
							<c:if test="${ undNota == 0 }">
								<c:set var="notaTutor" value="${ matricula.notaTutor }" />
							</c:if>
							
							<c:if test="${ undNota == 1 }">
								<c:set var="notaTutor" value="${ matricula.notaTutor2 }" />
							</c:if> 
							
							<c:if test="${ notaTutor != null 
										&& ((usuario.discenteAtivo.id == matricula.discente.id && !matricula.ocultarNotas)
										|| (usuario.discenteAtivo.id != matricula.discente.id))}">
								<fmt:formatNumber pattern="#0.0" value="${ notaTutor }" />
							</c:if>						
						</td>
						
							
						<td class="nota" style="border-right: 1px solid #888;">
							<c:if test="${ matricula.notas[undNota].nota != null && notaTutor != null}">
								<c:if test="${ ((not empty matricula.notas) || (matricula.notas[undNota] != null) || (matricula.notas[undNota].id != 0)) 
									&& ((usuario.discenteAtivo.id == matricula.discente.id && !matricula.ocultarNotas)
									|| (usuario.discenteAtivo.id != matricula.discente.id))}">
									<c:set var="mediaProf" value="${ (matricula.notas[undNota].nota * matricula.metodologiaAvaliacao.porcentagemProfessor) / 100 }" />
									<c:set var="mediaTut" value="${ (notaTutor * matricula.metodologiaAvaliacao.porcentagemTutor) / 100 }" />
									<fmt:formatNumber pattern="#0.0" value="${ (mediaProf + mediaTut) }" />
								</c:if>	
							</c:if>																
						</td>
						</c:if>
				</c:forEach>
					<c:if test="${ !relatorioNotasAluno.discente.lato }">
						<td class="nota" style="border-right: 1px solid #888;">
							<c:if test="${ !matricula.consolidada and ( usuario.discenteAtivo.id != matricula.discente.id
								or (usuario.discenteAtivo.id == matricula.discente.id and !matricula.ocultarNotas))}"	>
								<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacaoPreenchida ? '' : matricula.recuperacaoPreenchida }"/>
							</c:if>
							<c:if test="${ matricula.consolidada }">
								<fmt:formatNumber pattern="#0.0" value="${ matricula.recuperacao }"/>
							</c:if>
						</td>
					</c:if>
				</c:if>

				<td class="nota" style="border-right: 1px solid #888;">
					<c:if test="${ ( ( relatorioNotasAluno.discente.graduacao &&  !matricula.cumpriu) 
						|| !relatorioNotasAluno.discente.graduacao ) && matricula.consolidada }">
					<c:if test="${ relatorioNotasAluno.nota }">
					<fmt:formatNumber pattern="#0.0" value="${ matricula.mediaFinal }"/>
					</c:if>
					</c:if>
					<c:if test="${ relatorioNotasAluno.conceito 
									&& ((usuario.discenteAtivo.id == matricula.discente.id && !matricula.ocultarNotas)
									|| (usuario.discenteAtivo.id != matricula.discente.id))}">
								${ matricula.conceitoChar }
					</c:if>
					<c:if test="${ relatorioNotasAluno.competencia 
									&& ((usuario.discenteAtivo.id == matricula.discente.id && !matricula.ocultarNotas)
									|| (usuario.discenteAtivo.id != matricula.discente.id))}">
								${ matricula.apto ? 'Apto' : 'Não Apto' }
					</c:if>

					<c:if test="${ !matricula.consolidada }">
					
						<c:if test="${ !relatorioNotasAluno.ead }">
							<c:if test="${ !matricula.cumpriu && !relatorioNotasAluno.conceito && !relatorioNotasAluno.competencia }">
							-- 
							</c:if>
							<c:if test="${ matricula.ocultarNotas && (relatorioNotasAluno.conceito || relatorioNotasAluno.competencia) }">
							-- 
							</c:if>
							<c:if test="${ matricula.cumpriu }">
							${ matricula.conceitoChar }
							</c:if>
						</c:if>
						<c:if test="${ relatorioNotasAluno.ead }">
							<fmt:formatNumber pattern="#0.0" value="${ matricula.media }"/>
						</c:if>
						
					</c:if>
				</td>
				<c:if test="${ !relatorioNotasAluno.ead }">
				<td class="nota" style="border-right: 1px solid #888;">
					<c:if test="${  ( relatorioNotasAluno.discente.graduacao &&  !matricula.cumpriu) 
						|| !relatorioNotasAluno.discente.graduacao  }">
						${ matricula.numeroFaltas == null ? 0 : matricula.numeroFaltas }
					</c:if>
				</td>
				</c:if>
						<c:if test="${ !relatorioNotasAluno.ead || matricula.consolidada }">
							<td class="situacao">${ matricula.situacaoMatricula.descricao }</td>
						</c:if>
						
						<c:if test="${ relatorioNotasAluno.ead && !matricula.consolidada }">
							<td class="situacao">${ matricula.situacaoAbrev}</td>
						</c:if>
			</tr>
			</c:forEach>
			</tbody>
			</table>
		</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
