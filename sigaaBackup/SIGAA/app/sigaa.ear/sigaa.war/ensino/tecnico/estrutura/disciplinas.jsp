<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Dados Gerais > Módulos > Disc. Complementares </h2>
<a4j:keepAlive beanName="estruturaCurricularTecnicoMBean"/>	

<h:form id="form">
	
	<%@include file="/ensino/tecnico/estrutura/include/info_curriculo_tecnico.jsp"%>

	<br/>

	<table class="formulario" style="width: 80%" >
	  <caption>Adicionando Disciplinas Eletivas</caption>
		<h:inputHidden value="#{estruturaCurricularTecnicoMBean.obj.id}" />
		<tbody>
			<tr>
				<th class="obrigatorio">Disciplina:</th>
				<td colspan="3">
					<h:inputHidden id="idDisciplina" value="#{estruturaCurricularTecnicoMBean.disciplinaComplementar.disciplina.id}"/>
					<h:inputText id="nomeDisciplina" value="#{estruturaCurricularTecnicoMBean.disciplinaComplementar.disciplina.nome}" size="80"/>
						<ajax:autocomplete source="form:nomeDisciplina" target="form:idDisciplina"
						baseUrl="/sigaa/ajaxDisciplina" className="autocomplete" indicator="indicatorDisciplina" 
						minimumCharacters="3" parameters="nivel=${sessionScope.nivel}" parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDisciplina" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Período de Oferta:</th>
				<td><h:inputText value="#{estruturaCurricularTecnicoMBean.disciplinaComplementar.periodoOferta}" size="3" maxlength="1"
					onkeyup="return formatarInteiro(this);"/></td>
			</tr>
		</tbody>		
		<tfoot>
		   	<tr>
				<td colspan="6">
					<h:commandButton value="Adicionar" action="#{estruturaCurricularTecnicoMBean.adicionarDisciplinaComplementar}" id="adicionarDisciplina" />
				</td>
		    </tr>
		</tfoot>
	</table>
<br />
	<center>
		<i style="color:gray">
			Obs.: O cadastro de disciplinas complementares (ou eletivas) não acrescenta <br>
				carga horária para a estrutura curricular.
		</i>
	</center>

<br />
	<div class="infoAltRem" style="width: 100%">
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>

	<table class="formulario" width="100%">
		<caption>Disciplinas Complementares Cadastradas</caption>
			<thead>
				<tr>
					<th width="80%">Disciplina</th>
					<th style="text-align: right;">C.H.</th>
					<th style="text-align: right;">Período de Oferta</th>
					<th></th>
				</tr>
			</thead>
			<c:choose>
				<c:when test="${not empty estruturaCurricularTecnicoMBean.obj.disciplinasComplementares}">
					<c:forEach var="linha" items="#{estruturaCurricularTecnicoMBean.obj.disciplinasComplementares}" varStatus="status"> 
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${linha.disciplina.detalhes.nome}</td>
							<td align="right">${linha.disciplina.detalhes.chTotal}</td>
							<td align="right">${linha.periodoOferta}</td>				
							<td width="20" colspan="2" align="right">
								<h:commandLink action="#{estruturaCurricularTecnicoMBean.removerDisciplinaComplementar}" onclick="#{confirmDelete}">
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover" />
									<f:param name="id" value="#{linha.disciplina.id}"/>
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
						<tr>
							<td colspan="5" style="text-align: center; color: red; ">Não foi adicionada nenhuma disciplina</td>
						</tr>
				</c:otherwise>
			</c:choose>
			<tfoot>
			   	<tr>
					<td colspan="6">
						<h:commandButton value="<< Voltar" action="#{estruturaCurricularTecnicoMBean.viewModulo}" id="modulo" />
						<h:commandButton value="Cancelar" action="#{estruturaCurricularTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
						<h:commandButton value="Avançar >>" action="#{estruturaCurricularTecnicoMBean.viewResumo}" id="submeterDisciplinaCompl" />
					</td>
			    </tr>
			</tfoot>
	</table>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>