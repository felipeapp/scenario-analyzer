<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO} %>">
		<li>Ofertas de Est�gio
			<ul>
				<li><h:commandLink action="#{ofertaEstagioMBean.iniciar}" value="Cadastrar Oferta de Est�gio" onclick="setAba('geral')" /></li>
				<li><h:commandLink action="#{ofertaEstagioMBean.iniciarConsulta}" value="Consultar Oferta de Est�gio" onclick="setAba('geral')" /></li>
			</ul>	
		</li>
		
		<li>Est�gios
			<ul>
				<li> <h:commandLink action="#{buscaEstagioMBean.iniciar}" value="Gerenciar Est�gios" onclick="setAba('geral')" /> </li>
			</ul>
		</li>
	</ufrn:checkRole>
</ul>