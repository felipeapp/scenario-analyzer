<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Registro de ${buscaRegistroDiploma.obj.livroRegistroDiploma.tipoRegistroDescricao } Individual </h2>

<table class="visualizacao" >
	<caption>Dados do Registro</caption>
	<tr>
		<th> Livro: </th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.livroRegistroDiploma.titulo}"/>
		</td>
		<th> Folha: </th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.folha.numeroFolha}"/>
		</td>
	</tr>
	
	<tr>
		<th>Discente:</th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.discente.nome}"/>
		</td>
		<th> Número do Registro: </th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.numeroRegistro}"/>
		</td>
	</tr>
	</tr>
	<tr>
		<th>Pai:</th>
		<td colspan="3">
			<h:outputText value="#{buscaRegistroDiploma.obj.discente.pessoa.nomePai}"/>
		</td>
	</tr>
	<tr>
		<th>Mãe:</th>
		<td colspan="3">
			<h:outputText value="#{buscaRegistroDiploma.obj.discente.pessoa.nomeMae}"/>
		</td>
	</tr>
	<tr>
		<th>Nascido em:</th>
		<td>
			<fmt:formatDate value="${buscaRegistroDiploma.obj.discente.pessoa.dataNascimento}" pattern="dd 'de' MMMMMMM 'de' yyyy"/>
		</td>
		<th>Identidade:</th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.discente.pessoa.identidade}"/>
		</td>
	</tr>
	<tr>
		<th>Naturalidade:</th>
		<td colspan="3">
			<h:outputText value="#{buscaRegistroDiploma.obj.discente.pessoa.municipio}"/>
			<h:outputText value="#{buscaRegistroDiploma.obj.discente.pessoa.municipioNaturalidadeOutro}"/>
		</td>
	</tr>
	<c:if test="${buscaRegistroDiploma.obj.discente.graduacao}">
		<tr>
			<th>Curso:</th>
			<td>
				<h:outputText value="#{buscaRegistroDiploma.obj.discente.curso.descricao}"/>
			</td>
			<th>Grau:</th>
			<td>
				<h:outputText value="#{buscaRegistroDiploma.discenteGraduacao.matrizCurricular.grauAcademico.descricao}"/>
			</td>
		</tr>
	</c:if>
	<c:if test="${buscaRegistroDiploma.obj.discente.stricto}">
		<tr>
			<th>Programa:</th>
			<td>
				<h:outputText value="#{buscaRegistroDiploma.obj.discente.curso}"/>
			</td>
			<th>Título:</th>
			<td>
				<h:outputText value="#{buscaRegistroDiploma.obj.discente.nivelDesc}"/>
			</td>
		</tr>
	</c:if>
	<tr>
		<th>Concluído em:</th>
		<td>
			<fmt:formatDate value="${buscaRegistroDiploma.obj.dataColacao}" pattern="dd/MM/yyyy"/>
		</td>
		<th> Expedido em: </th>
		<td>
			<fmt:formatDate value="${buscaRegistroDiploma.obj.dataExpedicao}" pattern="dd/MM/yyyy"/>
		</td>
	</tr>
	<tr>
		<th>Reitor:</th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.assinaturaDiploma.nomeReitor}"/>
		</td>
		<th valign="top">Estabelecimento:</th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.livroRegistroDiploma.instituicao}"/>
		</td>
	</tr>
	<tr>
		<th>Diretor:</th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.diretorRespectivo}"/>
		</td>
		<th> Data do Registro: </th>
		<td >
			<fmt:formatDate value="${buscaRegistroDiploma.obj.dataRegistro}" pattern="dd/MM/yyyy"/>
		</td>
	</tr>
	<tr>
		<th>Registrado por:</th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.registroEntrada.usuario.pessoa.nome}"/><br/>
			<ufrn:format valor="${buscaRegistroDiploma.obj.criadoEm}" type="dataHoraSec" />
		</td>
		<th> Processo: </th>
		<td>
			<h:outputText value="#{buscaRegistroDiploma.obj.processo}"/>
		</td>
	</tr>
	<tr>
		<th valign="top"> Observação: </th>
		<td colspan="3">
			<c:forEach items="#{buscaRegistroDiploma.obj.observacoesAtivas}" var="item">
				<h:outputText value="#{item.observacao}"/>&nbsp;
			</c:forEach>
		</td>
	</tr>
	<c:if test="${ not empty buscaRegistroDiploma.alteracoesRegistroDiploma }" >
		<c:forEach items="#{buscaRegistroDiploma.alteracoesRegistroDiploma}" var="item">
			<tr>
				<td class="subFormulario" colspan="4"> 
					Alterações do Registro Realizada no Dia <ufrn:format type="datahora" valor="${item.criadoEm }" />
				</td>
			</tr>
			<tr>
				<th>Data de Expedição Anterior:</th>
				<td>
					<ufrn:format type="data" valor="${item.dataExpedicao }" />
					<h:outputText value="Valor não alterado" rendered="#{ empty item.dataExpedicao }" />
				</td>
				<th>Data de Registro Anterior:</th>
				<td>
					<ufrn:format type="data" valor="${item.dataRegistro }" />
					<h:outputText value="Valor não alterado" rendered="#{ empty item.dataRegistro }" />
				</td>
			</tr>
			<tr>
				<th>Nº do Processo Anterior:</th>
				<td>
					${item.processo }
					<h:outputText value="Valor não alterado" rendered="#{ empty item.processo }" />
				</td>
				<th>Alterado por:</th>
				<td>${item.registroEntrada.usuario.pessoa.nome }</td>
			</tr>
		</c:forEach>
	</c:if>
</table>
<br />
<div align="center">
	<a href="javascript:history.go(-1);"> << Voltar </a>
</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>