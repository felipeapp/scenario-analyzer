<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO} %>">
		<li>Ofertas de Estágio
			<ul>
				<li><h:commandLink action="#{ofertaEstagioMBean.iniciar}" value="Cadastrar Oferta de Estágio" onclick="setAba('geral')" /></li>
				<li><h:commandLink action="#{ofertaEstagioMBean.iniciarConsulta}" value="Consultar Oferta de Estágio" onclick="setAba('geral')" /></li>
			</ul>	
		</li>
		
		<li>Estágios
			<ul>
				<li> <h:commandLink action="#{buscaEstagioMBean.iniciar}" value="Gerenciar Estágios" onclick="setAba('geral')" /> </li>
			</ul>
		</li>
	</ufrn:checkRole>
</ul>