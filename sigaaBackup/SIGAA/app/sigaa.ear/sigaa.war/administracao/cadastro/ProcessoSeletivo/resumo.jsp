<style>
.unidade {
	font-size: 0.9em;
	font-style: italic;
	line-height: 1.3em;
}

.nivel {
	text-transform: uppercase;
}

.periodo {
	font-size: 1.1em;
	color: #292;
	font-weight: bold;
}

table.visualizacao td.texto {
	padding: 10px 20px;
	line-height: 1.3em;
}

table.visualizacao td.texto p {
	text-indent: 3em;
}

table.visualizacao td.inscrever {
	background: #CFC;
	text-align: center;
}

table.visualizacao td.inscrever a {
	color: #292;
	font-size: 1.2em;
	font-variant: small-caps;
	text-decoration: underline;
	line-height: 1.5em;
}

pre {
	white-space: normal;
}
</style>

<h2><ufrn:subSistema /> > Processo Seletivo</h2>
<h:form>
	<table class="visualizacao" style="width: 95%;">
		<caption>Dados do Processo Seletivo</caption>
		<c:choose>
			<%-- SE O PROCESSO SELETIVO FOR PÓS-GRADUAÇÃO OU LATOS SENSU --%>
			<c:when test="${not empty processoSeletivo.obj.curso}">
				<tr>
					<th valign="top">Curso:</th>
					<td>
					${processoSeletivo.obj.curso.descricao} <br />
					<span class="unidade">${processoSeletivo.obj.curso.unidade.nome}</span>
					</td>
				</tr>
				<tr>
					<th>Nível:</th>
					<td class="nivel">${processoSeletivo.obj.curso.nivelDescricao}</td>
				</tr>
				
			</c:when>
			<%-- SE PROCESSO SELETIVO FOR PARA TRANSFERÊNCIA VOLUNTÁRIA --%>
			<c:otherwise>
				<tr>
				<th valign="top">Curso:</th>
				<td>
					${processoSeletivo.obj.matrizCurricular.curso.descricao} <br />
					<span class="unidade">${processoSeletivo.obj.matrizCurricular.curso.unidade.nome}</span>
					</td>
				</tr>
				<tr>
					<th>Nível:</th>
					<td class="nivel">${processoSeletivo.obj.matrizCurricular.curso.nivelDescricao}</td>
				</tr>			
				
			</c:otherwise>
		</c:choose>
		<tr>
			<th>Período de Inscrições:</th>
			<td class="periodo">
				<h:outputText value="#{processoSeletivo.obj.editalProcessoSeletivo.descricaoInicioInscricoes}"/> -
				<h:outputText value="#{processoSeletivo.obj.editalProcessoSeletivo.descricaoFimInscricoes}"/>
			</td>
		</tr>
		
		<c:if test="${not empty processoSeletivo.obj.vaga && processoSeletivo.obj.vaga>0}">
		<tr>
			<th>Número de Vagas:</th>
			<td>${processoSeletivo.obj.vaga}</td>
		</tr>
			<c:if test="${processoSeletivo.obj.editalProcessoSeletivo.verificaExisteVaga}">
			<tr>
				<th>Número de Vagas Restantes:</th>
				<td style="color:green;">
					<strong>${processoSeletivo.obj.vagaRestante}</strong>
				</td>
			</tr>
			</c:if>
		</c:if>
		
		<c:if test="${not empty processoSeletivo.obj.questionario && processoSeletivo.obj.questionario.ativo}">
			<th>Questionário Específico:</th>
			<td>
				<h:commandLink value="#{processoSeletivo.obj.questionario.titulo}" action="#{questionarioBean.view}">
					<f:param name="id" value="#{processoSeletivo.obj.questionario.id}" />
				</h:commandLink>				
			</td>
		</c:if>

		<c:if test="${not empty processoSeletivo.obj.editalProcessoSeletivo.idEdital}">
			<tr>
				<th valign="middle" align="right">
					<h:graphicImage value="/img/icones/document_view.png" style="overflow: visible;" />
				</th>
				<td>
				<a href="${ctx}/verProducao?idProducao=${ processoSeletivo.obj.editalProcessoSeletivo.idEdital}&key=${ sf:generateArquivoKey(processoSeletivo.obj.editalProcessoSeletivo.idEdital) }"
					target="_blank">Clique AQUI para ler o Edital do Processo!</a></td>
			</tr>
		</c:if>

		<c:if test="${not empty processoSeletivo.obj.editalProcessoSeletivo.idManualCandidato}">
			<tr>
				<th valign="middle" align="right">
					<h:graphicImage value="/img/icones/document_view.png" style="overflow: visible;" />
				</th>
				<td>
				<a href="${ctx}/verProducao?idProducao=${ processoSeletivo.obj.editalProcessoSeletivo.idManualCandidato}&key=${ sf:generateArquivoKey(processoSeletivo.obj.editalProcessoSeletivo.idManualCandidato) }"
					target="_blank">Clique AQUI para ler o Manual do Candidato!</a></td>
			</tr>
		</c:if>

		<c:if test="${empty comprovante and processoSeletivo.obj.inscricoesAbertas}">
			<h:outputText value="#{inscricaoSelecao.create}" />
			<tr>
				<th valign="middle" align="right">
					<h:graphicImage value="/public/images/icones/selecao.gif" style="overflow: visible;width:32px;" />
				</th>
				<td valign="center" align="left">
				<h:commandLink title="Inscrever-se neste processo seletivo"  action="#{inscricaoSelecao.iniciarInscricao}">
					<f:param name="id" value="#{processoSeletivo.obj.id}" />
					<b>Clique AQUI para inscrever-se!</b>
				</h:commandLink>
				</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario">
				Descrição e Orientações aos Candidatos
			</td>
		</tr>
		<tr>
			<td colspan="2" class="texto">
				${processoSeletivo.obj.editalProcessoSeletivo.descricao}
			</td>
		</tr>
		<c:if test="${not empty processoSeletivo.obj.editalProcessoSeletivo.orientacoesInscritos}">
		<tr>
			<td colspan="2" class="subFormulario">Orientações aos Inscritos
			</td>
		</tr>
		<tr>
			<td colspan="2" class="texto">
			${processoSeletivo.obj.editalProcessoSeletivo.orientacoesInscritos}</td>
		</tr>
		</c:if>
	</table>
	<c:if test="${empty comprovante}">
		<center>
		<br />
		<h:commandLink value="<< Voltar" action="#{processoSeletivo.redirectUrl}"/> 
		</center>
	</c:if>
</h:form>
