<table>
	<tbody>

	<tr>
		<th  style="text-align: left">Componente Curricular</th>
		<th width="5%">Créditos</th>
		<th width="15%" style="text-align: center">&nbsp;Horário</th>
		<th width="5%">&nbsp;Alunos*</th>
	</tr>
	
	<c:set var="nivelAtual" value="" />
	<c:forEach items="#{turmasVirtuais}" var="turma" varStatus="status">
		<!-- agrupando as turmas por nivel -->
		<c:if test="${nivelAtual != turma.disciplina.nivelDesc}">
			<c:set var="nivelAtual" value="${turma.disciplina.nivelDesc}"/>
			<tr class="nivel">
				<td colspan="4">
					${turma.disciplina.nivelDesc}
				</td>
			</tr>
		</c:if> 
	
		<tr class="${status.index % 2 == 0 ? "odd" : "" }">
			<td class="descricao" colspan="4">
				<c:if test="${!turma.infantil}">
					<h:commandLink action="#{turmaVirtual.entrar}" value="#{turma.disciplina.codigo} - #{turma.disciplina.nome} - T#{turma.codigo}">
						<f:param name="idTurma" value="#{turma.id}"/>
					</h:commandLink>  
				</c:if>
				<c:if test="${turma.infantil}">
					<c:if test="${turma.descricaoHorario == 'M'}">
						 <h:commandLink action="#{turmaInfantilMBean.listarAlunos}" value="#{turma.disciplina.codigo} - #{turma.disciplina.nome} - Manhã - #{turma.codigo}">
							<f:param name="id" value="#{turma.id}"/>
						</h:commandLink>
					</c:if>
					<c:if test="${turma.descricaoHorario == 'T'}">
						 <h:commandLink action="#{turmaInfantilMBean.listarAlunos}" value="#{turma.disciplina.codigo} - #{turma.disciplina.nome} - Tarde - #{turma.codigo}">
							<f:param name="id" value="#{turma.id}"/>
						</h:commandLink>
					</c:if>
					  
				</c:if>
				<span class="situacaoTurma">(${turma.situacaoTurma.descricao})</span>
				<c:if test="${turma.disciplina.permiteHorarioFlexivel}">
					<h:outputText value="**" />
				</c:if>
			</td>
		</tr>
		<tr class="${status.index % 2 == 0 ? "odd" : "" }">
			<td  class="info" style="text-align: left;">
				<c:choose>
					<c:when test="${not empty turma.subturmas}">
						<c:forEach items="#{turma.subturmas}" var="subTurma">
						${subTurma.ano}.${subTurma.periodo} 
						<c:set var="localTurma" value="" />
						<c:if test="${ empty subTurma.polo }">
							<c:set var="localTurma" value="${subTurma.local}" />
						</c:if>
						<c:if test="${ not empty subTurma.polo }">
							<c:set var="localTurma" value="${ subTurma.polo.descricao }" />
						</c:if>
						<c:if test="${not empty localTurma}">
							 T${subTurma.codigo } - Local: ${localTurma}
						</c:if>
						<span class="situacaoTurma">(${subTurma.situacaoTurma.descricao})</span>
						<br />
					</c:forEach>
					</c:when>
					<c:otherwise>
						${turma.ano}.${turma.periodo} 
						<c:set var="localTurma" value="" />
						<c:if test="${ empty turma.polo }">
							<c:set var="localTurma" value="${turma.local}" />
						</c:if>
						<c:if test="${ not empty turma.polo }">
							<c:set var="localTurma" value="${ turma.polo.descricao }" />
						</c:if>
						<c:if test="${not empty localTurma}">
							 Local: ${localTurma}
						</c:if>
					</c:otherwise>
				</c:choose>
			</td>
			<td class="info" style="text-align: center;">
				<c:choose>
					<c:when test="${turma.tecnico}">
						<ufrn:format type="valorint" valor="${turma.disciplina.detalhes.chTotal / 15}" /> 
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty turma.subturmas}">
								<c:forEach items="#{turma.subturmas}" var="subTurma">
									${subTurma.disciplina.detalhes.crTotal}  <br />
								</c:forEach>
							</c:when>
							<c:otherwise>
								${turma.disciplina.detalhes.crTotal} 
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</td>
			<td class="info" style="text-align: center">
				<c:choose>
					<c:when test="${not empty turma.subturmas}">
						<c:forEach items="#{turma.subturmas}" var="subTurma">
							<c:if test="${subTurma.disciplina.permiteHorarioFlexivel}">
								<h:form id="formSubTurma">
									<p:lightBox iframe="true" width="800" height="600" opacity="0.25" speed="150">  
							        	${subTurma.descricaoHorarioSemanaAtual}
								    	<h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf?idTurma=#{subTurma.id}">  
								        	<h:graphicImage value="/img/view_calendario.png" title="Ver Horário Completo da Turma" />  
								    	</h:outputLink>  
									</p:lightBox>  
								</h:form>
							</c:if>
							<c:if test="${!subTurma.disciplina.permiteHorarioFlexivel}">
								${subTurma.descricaoHorarioSemanaAtual} <br />
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:if test="${turma.disciplina.permiteHorarioFlexivel}">
							<h:form id="formTurma">
								<p:lightBox iframe="true" width="800" height="600" opacity="0.25" speed="150">
									${turma.descricaoHorarioSemanaAtual}
								    <h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf?idTurma=#{turma.id}">  
								        <h:graphicImage value="/img/view_calendario.png" title="Ver Horário Completo da Turma" />  
								    </h:outputLink>  
								 </p:lightBox>  
					        </h:form>
						</c:if>
						<c:if test="${!turma.disciplina.permiteHorarioFlexivel}">
							${turma.descricaoHorarioSemanaAtual}
						</c:if>
					</c:otherwise>
				</c:choose>
			</td>
			<td class="info" style="text-align: center">
				<c:choose>
					<c:when test="${not empty turma.subturmas}">
						<c:forEach items="#{turma.subturmas}" var="subTurma">
							${ subTurma.totalMatriculados } / ${ subTurma.capacidadeAluno } <br />
						</c:forEach>
					</c:when>
					<c:otherwise>
						${ turma.totalMatriculados } / ${ turma.capacidadeAluno }
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>

<span class="mais" style="text-align: left;">
	* Total de alunos matriculados / Capacidade da turma<br/>
	** A turma possui horário flexível e o horário exibido é da semana atual.
</span>
