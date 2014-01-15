<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="relatoriosCoordenador"></a4j:keepAlive>
<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<h2> <ufrn:subSistema /> &gt; ${relatoriosCoordenador.tituloRelatorio } </h2>
		<h:form id="anoPeriodoForm">
			<h:inputHidden value="#{relatoriosCoordenador.tipoRelatorio}"/>
			<table align="center" class="formulario" width="50%" >
				<caption class="listagem">Dados do Relat�rio</caption>
				<tbody>
				<c:if test="${relatoriosCoordenador.exibeCurso}">
					<tr>
						<c:if test="${acesso.secretarioDepartamento || acesso.chefeDepartamento  }">
							<th>Unidade:</th>
						</c:if>
						<c:if test="${!acesso.secretarioDepartamento && !acesso.chefeDepartamento }">
							<th>Curso:</th>
						</c:if>
						<td>
							<c:choose>
								<c:when test="${(acesso.coordenadorCursoGrad || acesso.secretarioGraduacao) }">
									${relatoriosCoordenador.cursoAtualCoordenacao.nomeCompleto} 
								</c:when>
								<c:when test="${acesso.secretarioDepartamento  || acesso.chefeDepartamento }">
									${sessionScope.usuario.vinculoAtivo.unidade.nome}
								</c:when>
								<c:when test="${acesso.dae || acesso.secretarioCentro } ">
									<h:selectOneMenu id="curso" value="#{relatoriosCoordenador.curso.id}" style="width: 90%;">
										<f:selectItems value="#{relatorioPorCurso.cursosCombo}" />
									</h:selectOneMenu>
								</c:when>
								<c:otherwise>
									${relatoriosCoordenador.cursoAtualCoordenacao } 
								</c:otherwise>
							</c:choose>						
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="obrigatorio">Ano-Per�odo:</th>
					<td>
						<h:inputText value="#{relatoriosCoordenador.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
						- <h:inputText value="#{relatoriosCoordenador.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
						<ufrn:help>Ano e per�odo que deseja buscar</ufrn:help>
					</td>
				</tr>
			  	<!--<c:if test="${acesso.dae || acesso.secretarioCentro || acesso.chefeDepartamento}"> 
					<tr>
						<th><h:selectBooleanCheckbox id="relatoriosEad" value="#{relatoriosCoordenador.ead}"/>
						</th>
						<td>
							Incluir os componentes de Educa��o � Dist�ncia (EAD).				
						</td>
					</tr>
			  </c:if>-->
				</tbody>
				<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:inputHidden value="#{relatoriosCoordenador.exibeCurso}"/>
						<h:inputHidden value="#{relatoriosCoordenador.exibeAnoPeriodoIngresso}"/>
						<h:commandButton value="Gerar Relat�rio" action="#{relatoriosCoordenador.submeterAnoPeriodo}" id="submeterAnoPeriodo" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatoriosCoordenador.cancelar}" id="cancelarAnoPeriodo" />
					</td>
				</tr>
				</tfoot>
			</table>
			<br>
			<center><html:img page="/img/required.gif" style="vertical-align: top;" /> 
				<span class="fontePequena">	Campos de preenchimento obrigat�rio. </span> 
			</center>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
