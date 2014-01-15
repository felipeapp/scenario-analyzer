<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Dados Gerais > M�dulos </h2>
<a4j:keepAlive beanName="estruturaCurricularTecnicoMBean"/>	
<h:form id="form">

	<div class="infoAltRem">
		<html:img page="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar M�dulo
	</div>

	<%@include file="/ensino/tecnico/estrutura/include/info_curriculo_tecnico.jsp"%>

	<br/>

	<table class="formulario" style="width: 80%" >
	  <caption>Adicionando M�dulos</caption>
		<h:inputHidden value="#{estruturaCurricularTecnicoMBean.obj.id}" />
		<tbody>
			<tr>
				<th width="20%" class="obrigatorio">M�dulo:</th>
				<td width="15%">
					<h:selectOneMenu value="#{estruturaCurricularTecnicoMBean.moduloCurricular.modulo.id}" id="modulo">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{moduloMBean.allModulosTecnico}" /> 
					</h:selectOneMenu>
				</td>

				<th width="15%" class="obrigatorio">Per�odo Oferta</th>
				<td width="5%">
					<h:inputText value="#{estruturaCurricularTecnicoMBean.moduloCurricular.periodoOferta}" size="3" maxlength="1" 
						onkeyup="return formatarInteiro(this);"/>
				</td>
				<td>
					<h:commandLink action="#{estruturaCurricularTecnicoMBean.adicionarModulo}" >
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar M�dulo"/>
					</h:commandLink>
				</td>
			</tr>
		</tbody>
		<c:if test="${not empty estruturaCurricularTecnicoMBean.obj.modulosCurriculares}">	
			<tr>
				<td colspan="5">
					<table class="subFormulario" width="100%">
					  <caption>M�dulos cadastrados</caption>
						<thead>
							<tr>
								<th>C�d.</th>
								<th>Descri��o</th>
								<th>C.H.</th>
								<th>Pr. Oferta</th>
								<th></th>
							</tr>
						</thead>
							<c:forEach var="linha" items="#{estruturaCurricularTecnicoMBean.obj.modulosCurriculares}" varStatus="status">
								<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td>${linha.modulo.codigo}</td>
									<td>${linha.modulo.descricao}</td>
 									<td>${linha.modulo.cargaHoraria}</td>
									<td>${linha.periodoOferta}</td>
									<td width="20">
										<h:commandLink action="#{estruturaCurricularTecnicoMBean.removerModulo}" onclick="#{confirmDelete}" >
											<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
											<f:param name="id" value="#{linha.modulo.id}"/>
										</h:commandLink>
									</td>						
								</tr>
							</c:forEach>
					</table>
				</td>
			</tr>
		</c:if>
		<tfoot>
		   	<tr>
				<td colspan="6">
					<h:commandButton value="<< Voltar" action="#{estruturaCurricularTecnicoMBean.dadosGerais}" id="dadosGerais" />
					<h:commandButton value="Cancelar" action="#{estruturaCurricularTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
					<h:commandButton value="Avan�ar >>" action="#{estruturaCurricularTecnicoMBean.submeterModulos}" id="avancar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>