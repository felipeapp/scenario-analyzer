<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Listagem das Estruturas Curriculares </h2>
<a4j:keepAlive beanName="estruturaCurricularTecnicoMBean"/>
<h:form id="form">

<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.PEDAGOGICO_TECNICO  }%>">
	<table class="formulario" style="width: 60%">
	  <caption>Resumo da Estrutura Curricular</caption>
		<h:inputHidden value="#{estruturaCurricularTecnicoMBean.obj.id}" />
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{estruturaCurricularTecnicoMBean.filtroCurso}" id="checkCurso" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label></td>
				<td>
					<h:selectOneMenu value="#{estruturaCurricularTecnicoMBean.obj.cursoTecnico.id}" id="curso" 
					onchange="$('form:checkCurso').checked = true;">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<c:choose>
							<c:when test="${acesso.pedagogico}">
								<f:selectItems value="#{estruturaCurricularTecnicoMBean.allCursoTecnicoCombo}" />
							</c:when>
							<c:otherwise>
								<f:selectItems value="#{curso.allCursoNivelAtualCombo}"/>
							</c:otherwise>
						</c:choose>
							
					</h:selectOneMenu>
				</td>
			</tr>
		  <tfoot>
			   <tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{estruturaCurricularTecnicoMBean.buscar}" id="buscar" />
					</td>
			   </tr>
 		  </tfoot>
    </table>
  
<br />  <br />

</ufrn:checkRole>

	<center>
		<div class="infoAltRem">
	        <h:graphicImage value="/img/view.gif"  style="overflow: visible;"/>: Visualizar Estrutura Curricular
			<c:if test="${not acesso.pedagogico }">
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Estrutura Curricular
        		<h:graphicImage value="/img/delete.gif"  style="overflow: visible;"/>: Remover Estrutura Curricular
        	</c:if>
		</div>
	</center>

	<table class="listagem">
		<caption>Estruturas Curriculares</caption>
			<thead>
				<tr>
					<td>Curso</td>
					<td style="text-align: right;">Cód.</td>
					<td style="text-align: right;">Ano-Per. Ent. Vigor</td>
					<td style="text-align: right;">C.H.</td>
					<td style="text-align: center;">Ativo</td>
					<td colspan="3"></td>
				</tr>
			</thead>		
		<c:forEach var="linha" items="#{estruturaCurricularTecnicoMBean.estruturas}" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>${linha.cursoTecnico.nome}</td>
				<td style="text-align: right;">${linha.codigo}</td>
				<td style="text-align: right;">${linha.anoEntradaVigor}.${linha.periodoEntradaVigor}</td>
				<td style="text-align: right;">${linha.chTotalModulos} h</td>
				<td style="text-align: center;"><ufrn:format type="simnao" valor="${linha.ativa}" /></td>
				<td width="20">
					<h:commandLink action="#{estruturaCurricularTecnicoMBean.view}" >
						<h:graphicImage value="/img/view.gif"style="overflow: visible;" title="Visualizar Estrutura Curricular"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>
				
				<c:if test="${not acesso.pedagogico}">
				<td width="20">
					<h:commandLink action="#{estruturaCurricularTecnicoMBean.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Estrutura Curricular"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>
				<td width="20">
					<h:commandLink action="#{estruturaCurricularTecnicoMBean.remover}" >
						<h:graphicImage value="/img/delete.gif"style="overflow: visible;" title="Remover Estrutura Curricular" onclick="#{confirmDelete}"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>
				</c:if>
			</tr>		
		</c:forEach>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>