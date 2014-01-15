<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.academico.dominio.NivelEnsino"%><f:view>
	<h2 class="title"><ufrn:subSistema /> > Consulta de Calend�rio Acad�mico</h2>
	<h:form id="form">
		<c:if test="${!calendario.nivelStricto && !calendario.portalDiscente}">
			<table class="formulario" width="70%">		    		
				<caption>Escolha os Par�metros</caption>
				<tr>
					<th align="right"><b>Unidade Respons�vel:</b></th>
					<td>					
						<h:selectOneMenu id="unidades" onchange="submit()" rendered="#{calendario.podeAlterarUnidade}"
							valueChangeListener="#{calendario.selecionarGestora}"
							value="#{calendario.obj.unidade.id}">
							<f:selectItems value="#{unidade.allGestorasAcademicasCombo}" />
						</h:selectOneMenu>
						<h:outputText value="#{calendario.obj.unidade.nome}" rendered="#{!calendario.podeAlterarUnidade}" />					
						
					</td>
				</tr>
				<tr>
					<th align="right"><b>N�vel de Ensino:</b></th>
						<td>
							<h:selectOneMenu onchange="submit()" rendered="#{calendario.podeAlterarNivel}"
							valueChangeListener="#{calendario.selecionarNivel}" id="niveis"  value="#{calendario.obj.nivel}"  >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{calendario.comboNiveis}" />
						</h:selectOneMenu>
						<c:if test="${!calendario.podeAlterarNivel}">${calendario.obj.nivelDescr}</c:if>
					</td>
				</tr>			
				<tr>
					<th>Modalidade de Ensino:</th>
					<td>
						<h:selectOneMenu id="modalidade" valueChangeListener="#{calendario.carregarCursosPorModalidade}"
							   onchange="submit()" value="#{calendario.obj.modalidade.id}" >
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{modalidadeEducacao.allCombo}" />
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Se n�o escolher uma modalidade ser�o
							exibidos os calend�rios independente da modalidade</ufrn:help>
					</td>
				</tr>
				<tr>
					<th>Conv�nio Acad�mico:</th>
					<td>
						<h:selectOneMenu id="convenio" valueChangeListener="#{calendario.carregarCursosPorConvenio}"
							 onchange="submit()" value="#{calendario.obj.convenio.id}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{convenioAcademico.allCombo}" />
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Se n�o escolher um conv�nio ser�o
						exibidos os calend�rios independente do conv�nio</ufrn:help>
					</td>
				</tr>
				<tr>
					<th>Curso:</th>
					<td>
						<h:selectOneMenu value="#{calendario.obj.curso.id}"
							id="curso">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{calendario.comboCursos}" />
						</h:selectOneMenu>		
						<ufrn:help>Caso n�o informar um curso, ser�o exibidos os calend�rios �nicos da ${ configSistema['siglaInstituicao'] }, utilizados por todos os cursos.</ufrn:help>			    
					</td>
				</tr>				
				<tr><td colspan="2">&nbsp;</td></tr>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="Buscar" id="buscar" action="#{calendario.buscarCalendarios}" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{calendario.cancelar}" id="cancelar" immediate="true"/>
						</td>
					</tr>
				</tfoot>
			</table>					
				
		</c:if>	
		<c:set var="calendarios" value="#{calendario.calendarios}"/>
		
		<c:if test="${!empty calendarios}">		
			<br/>
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Calend�rio Acad�mico
			</div>		
			
			<br>
			
			<table style="width: 100%" class="listagem">
				<caption class="listagem">Lista de Calend�rios</caption>
				<tbody>
					<c:set var="semestre" />
					<c:forEach items="#{calendarios}" var="item" varStatus="status">			
					<c:set var="semestreAtual" value="${item.ano}.${item.periodo}"/>
					<c:if test="${semestre != semestreAtual}">
					<thead>
						<tr><td>Calend�rio ${item.ano}.${item.periodo}</td><td></td></tr>
					</thead>		
					<c:set var="semestre" value="${item.ano}.${item.periodo}"/>
					</c:if>
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>
							<c:if test="${empty item.modalidade.descricao}"> CALEND�RIO GERAL
								<c:if test="${!empty item.convenio.descricao}"> 
								(Conv�nio ${item.convenio.descricao})</c:if> 
							</c:if> 
							<c:if test="${!empty item.modalidade.descricao}"> ENSINO ${fn:toUpperCase(item.modalidade.descricao) }
								<c:if test="${!empty item.convenio.descricao}">
									( Conv�nio ${item.convenio.descricao})
								</c:if>
							</c:if>
						</td>
						<td align="right">
							<h:commandLink title="Visualizar Calend�rio Acad�mico" id="visualizar" action="#{calendario.view}">
									<h:graphicImage url="/img/view.gif"/>
									<f:param name="id" value="#{item.id}"/>
								</h:commandLink>
							</td>							
						</tr>
					</c:forEach>				
				</tbody>
			</table>	
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
