<table class="listagem" style="margin-top: 5px;">
	<tr>
		<td class="subFormulario">Disciplinas Matriculadas</td>
	</tr>		
	
	<tr>
		<td>
			<c:set var="turmasAbertas" value="#{ portalFamiliar.disciplinasAbertas }"/>
			<c:if test="${empty turmasAbertas}">
				<p class="vazio" style="padding: 5px;"> <i> Nenhuma disciplina matriculada.</i></p>
			</c:if>
			<c:if test="${!empty turmasAbertas}">
			<table style="width: 100%">
				<thead>
				<tr>
					<th width="30%" style="text-align:left">Disciplina</th>
					<th width="40%" style="text-align:left">Docente</th>
					<th width="25%" style="text-align:center">Horário</th>
					<th colspan="2" width="1%"></th>
				</tr>
				</thead>
				<tbody>
				<c:set var="idSerie" value="0"></c:set>
				<c:forEach items="#{turmasAbertas}" var="t" varStatus="status">
				
					<c:if test="${idSerie != t.turmaSerie.id }">
						<tr>
							<td colspan="4" class="subFormulario">${t.turmaSerie.descricaoCompleta}</td>
						</tr>
					</c:if>
				
					<c:set var="idSerie" value="${t.turmaSerie.id }"></c:set>
					
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td class="descricao">
							${t.turma.disciplina.nome} ${t.descricaoDependencia}
						</td>
						<td class="info" style="text-align:left">${t.turma.docentesNomes}</td>
						<td class="info">
							<center>${t.turma.descricaoHorarioSemanaAtual}
								<c:if test="${t.turma.disciplina.permiteHorarioFlexivel}">
									*
									<c:set var="possuiTurmaHorarioFlexivel" value="true" />
									<p:lightBox iframe="true" width="700" height="480" opacity="0.25" speed="150" style="display:inline;">  
									    <h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf?idTurma=#{t.turma.id}">  
									        <h:graphicImage value="/img/view_calendario.png" title="Ver Horário Completo da Turma" />  
									    </h:outputLink>  
									</p:lightBox>  
								</c:if>											
							</center>
						</td>
						<td>
							<h:commandLink id="btFreqTurma" action="#{ portalFamiliar.acessarFrequencia }" title="Frequências">
								<f:param name="idTurma" value="#{ t.turma.id }"/>
								<h:graphicImage value="/img/portal_familiar/frequencia.png" style="width:16px;"></h:graphicImage>
							</h:commandLink> 												
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
			<c:if test="${possuiTurmaHorarioFlexivel}" >
				<br>
				<span class="mais" style="text-align: center;">
					* A turma possui horário flexível e o horário exibido é da semana atual.
				</span>
			</c:if>
			</c:if>
		</td>
	</tr>		
</table>	