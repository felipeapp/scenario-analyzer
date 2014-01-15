<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName = "espacoFisicoBean"/>

	<h2><ufrn:subSistema/> > Espaço Físico </h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Espaço Físico.</strong>
		</p>
		<br/>
		<p>
			O cadastro foi dividido em 2 etapas:
			<ol>
				<li><strong>A primeira etapa é responsável por descrever o espaço físico.</strong></li>
				<li>Durante a segunda etapa, detalhe os recursos que o espaço físico possui.</li>
			</ol>
		</p>
	
	</div>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption>Dados do Espaço Físico</caption>
		<tbody>
			<tr>
				<th class="required" width="25%">Código Identificador:</th>
				<td>
					<h:inputText id="codigo" value="#{espacoFisicoBean.obj.codigo}" onkeyup="CAPS(this);" size="50" style="width: 90%"  />
					<ufrn:help width="200">
						Código único que identifique este espaço. Exemplo: 4A4 
					</ufrn:help>
				</td>
			</tr>					
			<tr>
				<th valign="top">Descrição do espaço:</th>
				<td><h:inputTextarea id="descricao" value="#{espacoFisicoBean.obj.descricao}" style="width: 90%" rows="3" /></td>
			</tr>
			<tr>
				<th class="required">Localização do espaço:</th>
				<td>
					<h:selectOneMenu id="unidadeResponsavel" value="#{espacoFisicoBean.obj.unidadeResponsavel.id}" style="width: 90%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE  --"/>
						<f:selectItems value="#{gestorUnidadesMBean.unidadesCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>						
			
			<tr>
				<th>Capacidade:</th>
				<td>
					<h:inputText id="capacidade" value="#{espacoFisicoBean.obj.capacidade}" size="6" onkeyup="return formatarInteiro(this);"/>
					<ufrn:help width="300">
						Número de pessoas que o espaço comporta
					</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Área:</th>
				<td><h:inputText id="area" value="#{espacoFisicoBean.obj.area}" size="6" onkeyup="return formatarInteiro(this);"/> m²</td>
			</tr>						
				<tr>
				<th class="required">Tipo:</th>
				<td>
					<h:selectOneMenu id="tipoEspacoFisico" value="#{espacoFisicoBean.obj.tipo.id}" style="width: 50%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE  --"/>
						<f:selectItems value="#{espacoFisicoBean.allTipoEspacoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>				
			
			<tr>
				<th>Espaço Reservado Prioritariamente para:</th>
				<td>
					<h:selectOneMenu id="unidadeReservada" value="#{espacoFisicoBean.obj.unidadePreferenciaReserva.id}" style="width: 50%">
					<f:selectItem itemValue="0" itemLabel="-- NENHUMA UNIDADE  --"/>
						<f:selectItems value="#{unidade.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Cancelar" action="#{espacoFisicoBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" immediate="true" />
					<h:commandButton value="Próximo >>" action="#{espacoFisicoBean.gerenciarRecursos}" id="proximo"/>
				</td>
			</tr>
		</tfoot>							
		</table>
	</h:form>

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>

</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>